class window.Agent
  CENTER = new PIXI.Point(0.5, 0.5)
  constructor: (texture) ->
    @graphics = new PIXI.Sprite texture
    @graphics.anchor = CENTER
    @graphics.scale.x =  @graphics.scale.y = 0.8
    @graphics.position = new PIXI.Point(0, -10)
    @step = 0

  tick: ()->
    @step += 1
    @graphics.rotation = Math.sin(@step * 0.1) * 0.2
