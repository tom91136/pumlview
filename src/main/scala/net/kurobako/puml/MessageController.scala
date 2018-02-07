package net.kurobako.puml

import javafx.beans.binding.{Bindings, BooleanBinding}

import scalafx.Includes.handle
import scalafx.scene.Node
import scalafx.scene.control._
import scalafx.scene.layout.{HBox, Priority, StackPane, VBox}
import scalafx.Includes._

class MessageController {

	case class Element(message: Message, action: Option[Message => Unit] = None)


	private val messages = new ListView[Element] {
		styleClass += "messages"
		managed <== visible
		cellFactory = { _ =>
			new ListCell[Element] {
				item.onChange { (_, _, n) => graphic = Option(n).map {mkElementView}.orNull }
			}
		}
	}

	val toolbar = new VBox {
		private val noMessages: BooleanBinding = Bindings.isEmpty(messages.getItems)
		styleClass ++= Seq("message", "toolbar")
		children = Seq(
			new Button("\uD83D\uDDD1") {
				onAction = handle {messages.getItems.clear()}
				disable <== noMessages
			},
			new Button("▲") {
				onAction = handle {messages.getSelectionModel.selectFirst()}
				disable <== noMessages
			},
			new Button("▼") {
				onAction = handle {messages.getSelectionModel.selectLast()}
				disable <== noMessages
			}
		)
	}

	private def mkElementView: Element => Node = {
		case Element(m@Message(kind, message, line), action) => new HBox {
			styleClass ++= Seq("message", kind match {
				case Info  => "info"
				case Error => "error"
			})
			children = Seq(
				new Label(kind match {
					case Info  => " \u2139 "
					case Error => " \u26A0 "
				}),
				new Label(line.map { l => s"Line $l: " }.getOrElse("") + message)
			)
			onMouseClicked = handle {action.foreach(_ (m))}
		}
	}

	def append(message: Message, action: Option[Message => Unit] = None): Unit = {
		Element(message, action) +=: messages.getItems
	}


	val root = new HBox {
		HBox.setHgrow(messages, Priority.Always)
		children = Seq(toolbar, messages)
	}


}
