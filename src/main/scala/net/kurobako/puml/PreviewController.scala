package net.kurobako.puml

import net.kurobako.gesturefx.GesturePane

import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.StackPane
import scalafx.Includes._
import scalafx.geometry.Pos
import scalafx.scene.control.Label
import scalafx.scene.web.WebView

class PreviewController {
	val root = new StackPane {
		alignment = Pos.Center
		children = new Label("No preview yet")
	}


	def accept(rendering: Rendering): Unit = {
		rendering match {
			case SVG(code)     => root.children = new WebView()
			case Raster(image) => root.children = new GesturePane(new ImageView(image))
		}
	}

}
