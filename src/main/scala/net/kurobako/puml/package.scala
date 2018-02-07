package net.kurobako

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import java.nio.charset.StandardCharsets

import net.sourceforge.plantuml.{FileFormat, FileFormatOption, SourceStringReader}
import net.sourceforge.plantuml.syntax.SyntaxChecker

import scala.util.{Failure, Success, Try}
import scalafx.scene.image.Image
import scala.collection.JavaConverters._

package object puml {

	sealed trait RenderTarget extends enumeratum.EnumEntry
	object RenderTarget extends enumeratum.Enum[RenderTarget] {
		override def values = findValues
		case object SVG extends RenderTarget
		case object PNG extends RenderTarget
	}


	sealed trait Rendering
	case class SVG(code: String) extends Rendering
	case class Raster(image: Image) extends Rendering

	type LineNumber = Int
	sealed trait MessageKind
	case object Info extends MessageKind
	case object Error extends MessageKind
	case class Message(kind: MessageKind, message: String, line: Option[LineNumber] = None)


	sealed trait Outcome
	case class Crashed(throwable: Throwable) extends Outcome
	case class SyntaxError(message: String, line: Option[Int] = None) extends Outcome
	case class Ok(rendering: Rendering, elapsed: Long) extends Outcome

	def compilePuml(source: String, target: RenderTarget): Outcome = {
		val start = System.currentTimeMillis()
		val result = SyntaxChecker.checkSyntax(source)
		if (result.isError) {
			SyntaxError(
				message = s"${result.getErrors.asScala.mkString(" ")}\n\t${result.getSuggest.asScala.mkString(" ")}",
				line = Some(result.getErrorLinePosition))
		} else Try {
			val stream = new ByteArrayOutputStream()
			new SourceStringReader(source)
				.generateImage(stream, new FileFormatOption(target match {
					case RenderTarget.SVG => FileFormat.SVG
					case RenderTarget.PNG => FileFormat.PNG
				}))
			stream.toByteArray
		} match {
			case Failure(e)     => Crashed(e)
			case Success(value) =>
				val elapsed = System.currentTimeMillis() - start
				Ok(rendering = target match {
					case RenderTarget.SVG => SVG(new String(value, StandardCharsets.UTF_8))
					case RenderTarget.PNG => Raster(new Image(new ByteArrayInputStream(value)))
				}, elapsed = elapsed)
		}
	}

}
