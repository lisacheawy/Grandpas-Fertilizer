package gf.game.model

import scalafx.scene.control.TextField
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.paint.Color
import scalafx.scene.layout.{Border, BorderStroke, BorderStrokeStyle, BorderWidths, CornerRadii}
import scalafx.scene.shape.StrokeLineCap.Square
import scalafx.scene.shape.{StrokeLineJoin, StrokeType}
import javafx.scene.{text => jfxst}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.text.Font

class MainMenuModel {
  val menuBackground = new Image(getClass.getResourceAsStream("/gf.game.model/menu.png"))
  val menuBackgroundView: ImageView = new ImageView(menuBackground) {
    fitWidth = 1344
    fitHeight = 960
  }

  val playButton = new Image(getClass.getResourceAsStream("/gf.game.model/play-button.png"))
  val playButtonView: ImageView = new ImageView(playButton) {
    x = 463
    y = 729
  }

  val borderStrokeStyle = new BorderStrokeStyle(
    strokeType = StrokeType.Centered,
    lineJoin = StrokeLineJoin.Bevel,
    lineCap = Square,
    miterLimit = 2,
    dashOffset = 6,
    dashArray = Seq(12)
  )

  val nameField: TextField = new TextField {
    layoutX = 463
    layoutY = 565
    minWidth = 440
    padding = Insets.Empty
    alignment = Pos.Center
    font = new Font(jfxst.Font.loadFont(getClass.getResourceAsStream("/gf.game.model/NanumPenScript-Regular.ttf"), 40))
    border = new Border(new BorderStroke(
      stroke = Color.Black,
      style = borderStrokeStyle,
      radii = new CornerRadii(10),
      widths = new BorderWidths(4)
    ))
    style = String.format("-fx-faint-focus-color: transparent; -fx-focus-color:rgba(255,0,0,0);")
  }

  var callFunc = false
}
