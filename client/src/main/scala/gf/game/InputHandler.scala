package gf.game

import akka.actor.ActorRef
import gf.game.model.SharedData
import scalafx.scene.input.{KeyCode, KeyEvent}

class InputHandler(inputEventsReceiver: ActorRef, sharedData: SharedData) {
  //manipulating movement based on boolean value.
  //directly manipulating based on keypress showed delay
  def keyPress(e: KeyEvent): Unit = {
    if (e.code == KeyCode.A) sharedData.leftPressed = true
    if (e.code == KeyCode.D) sharedData.rightPressed = true
    if (e.code == KeyCode.W) sharedData.upPressed = true
    if (e.code == KeyCode.S) sharedData.downPressed = true
    if (e.code == KeyCode.Escape) sharedData.escapePressed = true
  }

  def keyRelease(e: KeyEvent): Unit = {
    if (e.code == KeyCode.A) sharedData.leftPressed = false
    if (e.code == KeyCode.D) sharedData.rightPressed = false
    if (e.code == KeyCode.W) sharedData.upPressed = false
    if (e.code == KeyCode.S) sharedData.downPressed = false
    if (e.code == KeyCode.Escape) sharedData.escapePressed = false
  }

  def addToInventory(item: String): Unit = {
    inputEventsReceiver ! s"inventory_add_${item}"
  }

  def removeFromInventory(item: String): Unit = {
    inputEventsReceiver ! s"inventory_remove_${item}"
  }

  def setState(state: String, value: Boolean): Unit = {
    inputEventsReceiver ! s"state_${state}_${value}"
  }

  def updatePlantedSpots(spot: String, overlay: String): Unit = {
    inputEventsReceiver ! s"spot_${spot}_${overlay}"
  }

  def updateWaterState(overlay: String, value: Boolean): Unit = {
    inputEventsReceiver ! s"water_${overlay}_${value}"
  }

  def setFertilizerSpot(spot: String): Unit = {
    inputEventsReceiver ! s"fertilizer_${spot}"
  }

  def updatePlantGrowth(plant: String, daysReq: Int, daysLeft: Int): Unit = {
    inputEventsReceiver ! s"grow_${plant}_${daysReq}_${daysLeft}"
  }

  def updateDayCounter(): Unit = {
    inputEventsReceiver ! s"day"
  }

  def movement(direction: String, steps: Int): Unit = {
    //sometimes, onKeyRelease is not detected if two consecutive buttons are pressed
    //this is an additional validation for that
    if (sharedData.currentDirection != direction) {
      sharedData.steps = 0
      sharedData.currentDirection = direction
    }
    sharedData.steps += 1
    inputEventsReceiver ! s"${direction}_${steps}"

  }

  def keyInput(): Unit = {
    if (sharedData.leftPressed){
      if (!sharedData.mudAudio.isPlaying) sharedData.mudAudio.play()
      movement("left", sharedData.steps)
    }
    else if (sharedData.rightPressed) {
      if (!sharedData.mudAudio.isPlaying) sharedData.mudAudio.play()
      movement("right", sharedData.steps)
    }
    else if (sharedData.upPressed) {
      if (!sharedData.mudAudio.isPlaying) sharedData.mudAudio.play()
      movement("up", sharedData.steps)
    }
    else if (sharedData.downPressed) {
      if (!sharedData.mudAudio.isPlaying) sharedData.mudAudio.play()
      movement("down", sharedData.steps)
    }
    else {  //if released
      if (sharedData.currentDirection != "") {  //only set once
        sharedData.steps = 0
        sharedData.currentDirection = ""
        inputEventsReceiver ! s"down_front"
      }
    }
  }


}
