package net.kurobako.puml.editor

import javafx.geometry.Pos
import javafx.scene.control.ScrollPane.ScrollBarPolicy
import javafx.scene.layout.HBox

import org.fxmisc.flowless.VirtualizedScrollPane
import org.fxmisc.richtext.{CodeArea, LineNumberFactory}


class CodePane extends VirtualizedScrollPane[CodeArea](
	new CodeArea(),
	ScrollBarPolicy.AS_NEEDED,
	ScrollBarPolicy.AS_NEEDED) {

	val area = getContent

	val mkLineNumber = LineNumberFactory.get(area)
	def mkArrow = new ArrowFactory(area.currentParagraphProperty())
	area.setParagraphGraphicFactory { line =>
		val box = new HBox(mkLineNumber(line), mkArrow(line))
		box.setAlignment(Pos.CENTER_LEFT)
		box
	}

	def text: String = area.getText()


}
