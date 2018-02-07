package net.kurobako.puml

import java.awt.Panel

import better.files._
import com.google.common.io.Resources

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.{Node, Scene}
import scalafx.scene.control._
import scalafx.stage.{FileChooser, Stage}
import scalafx.Includes._
import scalafx.geometry.{Orientation, Pos}
import scalafx.scene.layout.{Region, VBox}
import scalafx.stage.FileChooser.ExtensionFilter


object Main extends JFXApp {


	private val root = new TabPane {
		styleClass += "root"
		tabClosingPolicy = TabPane.TabClosingPolicy.AllTabs
	}
	def mkSession(file: Option[File]) = new Tab {
		private val preview = new PreviewController()
		private val editor  = new EditorController(preview.accept)
		private val frame   = new SplitPane {
			maxWidth = Double.MaxValue
			maxHeight = Double.MaxValue
			orientation = Orientation.Horizontal
			dividerPositions = Seq(0.5, 0.5): _*
			items ++= Seq(editor.root, preview.root)
		}
		editor.path.value = file
		text <== editor.title
		content = frame
		closable = true
	}
	def mkDashboard() = new Tab {
		text = " + "
		closable = false
		content = new VBox {
			styleClass += "splash"
			alignment = Pos.Center
			def addAndSelect(file: Option[File]): Unit = {
				root.tabs.add(1, mkSession(file))
				root.getSelectionModel.select(1)
			}
			children = Seq(
				new Label("Paste or drop file here to open") {
					styleClass += "hint"
				},
				new Button("Open...") {
					onAction = handle {
						Option(new FileChooser {
							extensionFilters ++= Seq(
								new ExtensionFilter("puml source", "*.puml"),
								new ExtensionFilter("any", "*")
							)
						}.showOpenDialog(stage))
							.map {_.toScala}
							.foreach(v => addAndSelect(Some(v)))
					}
				},
				new Button("Create new") {
					onAction = handle {addAndSelect(None)}
				}
			)
		}
	}

	root.tabs += mkDashboard()


	stage = new PrimaryStage {
		title = "PUML viewer"
		scene = new Scene(root, 800, 550) {
			stylesheets += Resources.getResource("styles.css").toExternalForm
		}
	}

}
