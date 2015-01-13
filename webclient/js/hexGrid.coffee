class window.HexGrid
  CELL_WIDTH = 65
  CELL_HEIGHT = 65
  ROW_STEP = 50

  CELL_TYPES = ['magic', 'dirt', 'rock', 'stone', 'sand', 'water']

  constructor: (@width, @height) ->
    @container = new PIXI.DisplayObjectContainer()
    cells_yx = [0...@height].map (y) =>
      [0...@width].map (x)=>
        center = new PIXI.Point (x + if y % 2 == 0 then 0.5 else 1) * CELL_WIDTH, (y + 0.5) * ROW_STEP
        type = CELL_TYPES[Math.floor(Math.random() * CELL_TYPES.length)]
        new window.Cell x, y, center, type, @container
    @cells = [0...@width].map (x) =>
      [0...@height].map (y) ->
        cells_yx[y][x]