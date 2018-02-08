package net.kurobako.puml

import javafx.geometry.Point2D

import net.kurobako.gesturefx.{AffineEvent, GesturePane}

import scalafx.Includes._
import scalafx.geometry.Pos
import scalafx.scene.Node
import scalafx.scene.control.Label
import scalafx.scene.image.ImageView
import scalafx.scene.layout.StackPane
import scalafx.scene.web.WebView

class PreviewController {
	val root = new StackPane {
		alignment = Pos.Center
		children = new Label("No preview yet")
	}


	def accept(rendering: Rendering): Unit = {
		rendering match {
			case SVG(code)     =>
				val webView = new WebView()
				val pane = new GesturePane
				val element = "document.getElementsByTagName('body')[0].style"
				pane.addEventHandler(AffineEvent.CHANGED, {
					e: AffineEvent =>
						val script =
							s"""$element.transform =
							   |'matrix(${e.namedCurrent().scaleX()},0,
							   |0,${e.namedCurrent().scaleY()},
							   |${e.namedCurrent().translateX()},
							   |${e.namedCurrent().translateY()})';
							   |""".stripMargin
						webView.engine.executeScript(script)
						()
				})

				webView.engine.documentProperty().onChange { (_, _, n) =>
					Option(n).foreach { _ =>
						pane.zoomTo(1, Point2D.ZERO)
						webView.engine.executeScript(s"$element.transformOrigin = '0 0 0';")
						webView.engine.executeScript(s"$element.overflow = 'hidden';")
						val w = webView.engine.executeScript("document.body.scrollWidth").toString.toDouble
						val h = webView.engine.executeScript("document.body.scrollHeight").toString.toDouble
						pane.setTarget(new GesturePane.Transformable() {
							override def width = w
							override def height = h
						})
					}
				}
				pane.pickOnBounds = true
				webView.engine.loadContent("<html><body>" + code + "</body></html>")
				root.children = new StackPane {children = Seq(webView, pane: Node)}
			case Raster(image) => root.children = new GesturePane(new ImageView(image))
		}
	}

}
