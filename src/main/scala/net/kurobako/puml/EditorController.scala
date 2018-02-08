package net.kurobako.puml

import better.files._
import net.kurobako.puml.editor.CodePane
import org.fxmisc.easybind.EasyBind
import org.fxmisc.easybind.monadic.MonadicBinding

import scalafx.Includes._
import scalafx.beans.property.ObjectProperty
import scalafx.geometry.Orientation
import scalafx.scene.Node
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control._
import scalafx.scene.layout.{Priority, VBox}
import scalafx.stage.FileChooser
import scalafx.stage.FileChooser.ExtensionFilter

class EditorController(val f: Rendering => Unit) {

	val path : ObjectProperty[Option[File]] = ObjectProperty(None)
	val title: MonadicBinding[String]       = EasyBind.map(path,
	{ op: Option[File] => op.flatMap(_.nameOption).getOrElse("New file*") })

	private val messages = new MessageController

	val root: SplitPane = new SplitPane

	private val editor  = new CodePane
	private val save    = new SplitMenuButton {
		text = "Save"
		items = Seq(new MenuItem("Save as..."))
		onAction = handle {
			path.value match {
				case Some(value) => value.overwrite(editor.text)
				case None        =>
					val selected = new FileChooser {
						extensionFilters ++= Seq(new ExtensionFilter("puml source", "*.puml"))
					}.showSaveDialog(root.getScene.getWindow).toScala
					if (selected.isRegularFile) {
						new Alert(AlertType.Confirmation, s"File $selected already exists, overwrite?",
							ButtonType.OK, ButtonType.Cancel).showAndWait() match {
							case Some(b) if b == ButtonType.OK =>
								path.value = Some(selected.overwrite(editor.text))
							case _                             =>
						}
					} else path.value = Some(selected.createIfNotExists().overwrite(editor.text))
			}

		}
	}
	private val target  = new ComboBox[RenderTarget](RenderTarget.values) {
		selectionModel.value.selectFirst()
	}
	private val compile = new Button("Compile") {
		onAction = handle {
			compilePuml(editor.text, target.getValue) match {
				case Crashed(throwable)         =>
					throwable.printStackTrace()
					messages.append(Message(Error, throwable.getMessage))
				case SyntaxError(message, line) =>
					messages.append(Message(Error, message, line), Some({
						case Message(_, _, Some(l)) => editor.area.moveTo(l, 0)
						case _                      => ()
					}))
				case Ok(rendering, elapsed)     =>
					messages.append(Message(Info, s"Compilation took ${elapsed}ms"))
					f(rendering)
			}
		}
	}
	private val wrap    = new ToggleButton("Wrap text")
	private val toolbar = new ToolBar() {
		items = Seq(save, wrap, target, compile)
	}

	editor.area.wrapTextProperty <== wrap.selected


	root.orientation = Orientation.Vertical
	root.maxWidth = Double.MaxValue
	root.maxHeight = Double.MaxValue
	root.dividerPositions = Seq(0.8, 0.2): _*


	VBox.setVgrow(editor, Priority.Always)

	SplitPane.setResizableWithParent(messages.root, value = false)
	root.items ++= Seq(
		new VBox {children = Seq(toolbar, editor: Node)}: Node,
		messages.root)

	path.onChange { (_, prev, next) =>
		(prev, next) match {
			case (None, Some(v)) => editor.area.appendText(v.contentAsString)
			case _               => ???
		}

	}


}
