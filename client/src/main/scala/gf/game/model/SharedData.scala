package gf.game.model
import scalafx.scene.media.AudioClip

class SharedData {
  var steps = 0
  var currentDirection = ""
  var leftPressed = false
  var rightPressed = false
  var upPressed = false
  var downPressed = false
  var escapePressed = false
  val mudAudio = new AudioClip(getClass.getResource("/gf.game.model/mudAudio.mp3").toExternalForm) {
    volume = 0.5
  }
}
