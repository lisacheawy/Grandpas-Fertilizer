package gf.game.model

import scalafx.scene.image.{Image, ImageView}

class PromptModel {
  val grandpaNote = new Image(getClass.getResourceAsStream("/gf.game.model/grandpa-note.png"))
  val grandpaNotePromptView: ImageView = new ImageView(grandpaNote)

  val instructions = new Image(getClass.getResourceAsStream("/gf.game.model/instructions.png"))
  val instructionsView: ImageView = new ImageView(instructions)

  val gameComplete = new Image(getClass.getResourceAsStream("/gf.game.model/game-complete.png"))
  val gameCompleteView: ImageView = new ImageView(gameComplete)

  val roomFullPrompt = new Image(getClass.getResourceAsStream("/gf.game.model/room-full-prompt.png"))
  val roomFullPromptView: ImageView = new ImageView(roomFullPrompt) {
    x = 407
    y = 344
  }

  val serverErrorPrompt = new Image(getClass.getResourceAsStream("/gf.game.model/server-error-prompt.png"))
  val serverErrorPromptView: ImageView = new ImageView(serverErrorPrompt) {
    x = 407
    y = 344
  }

  val nextDayPrompt = new Image(getClass.getResourceAsStream("/gf.game.model/next-day-prompt.png"))
  val nextDayPromptView: ImageView = new ImageView(nextDayPrompt) {
    x = 407
    y = 344
  }

  val useFertilizer = new Image(getClass.getResourceAsStream("/gf.game.model/use-fertilizer-prompt.png"))
  val useFertilizerPromptView: ImageView = new ImageView(useFertilizer) {
    x = 407
    y = 344
  }

  val wcPrompt = new Image(getClass.getResourceAsStream("/gf.game.model/wc-prompt.png"))
  val wcPromptView: ImageView = new ImageView(wcPrompt) {
    x = 407
    y = 344
  }

  val refillPrompt = new Image(getClass.getResourceAsStream("/gf.game.model/refill-prompt.png"))
  val refillPromptView: ImageView = new ImageView(refillPrompt) {
    x = 407
    y = 344
  }

  val fullPrompt = new Image(getClass.getResourceAsStream("/gf.game.model/full-prompt.png"))
  val fullPromptView: ImageView = new ImageView(fullPrompt) {
    x = 407
    y = 344
  }

  val emptyPrompt = new Image(getClass.getResourceAsStream("/gf.game.model/empty-prompt.png"))
  val emptyPromptView: ImageView = new ImageView(emptyPrompt) {
    x = 407
    y = 344
  }

  val noWCPrompt = new Image(getClass.getResourceAsStream("/gf.game.model/no-wc-prompt.png"))
  val noWCPromptView: ImageView = new ImageView(noWCPrompt) {
    x = 407
    y = 344
  }

  val plantPrompt = new Image(getClass.getResourceAsStream("/gf.game.model/plant-prompt.png"))
  val plantPromptView: ImageView = new ImageView(plantPrompt) {
    x = 407
    y = 344
  }

  val waterPrompt = new Image(getClass.getResourceAsStream("/gf.game.model/water-prompt.png"))
  val waterPromptView: ImageView = new ImageView(waterPrompt) {
    x = 407
    y = 344
  }

  val playerPlantingPrompt = new Image(getClass.getResourceAsStream("/gf.game.model/player-planting-prompt.png"))
  val playerPlantingPromptView: ImageView = new ImageView(playerPlantingPrompt) {
    x = 407
    y = 344
  }

  val playerWateringPrompt = new Image(getClass.getResourceAsStream("/gf.game.model/player-watering-prompt.png"))
  val playerWateringPromptView: ImageView = new ImageView(playerWateringPrompt) {
    x = 407
    y = 344
  }

  val fertilizerUsedPrompt = new Image(getClass.getResourceAsStream("/gf.game.model/fertilizer-used-prompt.png"))
  val fertilizerUsedPromptView: ImageView = new ImageView(fertilizerUsedPrompt) {
    x = 407
    y = 344
  }

  val yesButton = new Image(getClass.getResourceAsStream("/gf.game.model/yes-button.png"))
  val yesButtonView: ImageView = new ImageView(yesButton) {
    x = 507
    y = 548
  }

  val noButton = new Image(getClass.getResourceAsStream("/gf.game.model/no-button.png"))
  val noButtonView: ImageView = new ImageView(noButton) {
    x = 720
    y = 548
  }

  val okButton = new Image(getClass.getResourceAsStream("/gf.game.model/ok-button.png"))
  val okButtonView: ImageView = new ImageView(okButton) {
    x = 615
    y = 548
  }

  val exitButton = new Image(getClass.getResourceAsStream("/gf.game.model/exit-button.png"))
  val exitButtonView: ImageView = new ImageView(exitButton) {
    x = 549
    y = 665
  }

}
