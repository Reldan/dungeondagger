// Generated by CoffeeScript 1.8.0
(function() {
  window.HexGrid = (function() {
    var CELL_HEIGHT, CELL_TYPES, CELL_WIDTH, ROW_STEP;

    CELL_WIDTH = 65;

    CELL_HEIGHT = 65;

    ROW_STEP = 50;

    CELL_TYPES = ['magic', 'dirt', 'rock', 'stone', 'sand', 'water'];

    function HexGrid(width, height) {
      var cells_yx, _i, _j, _ref, _ref1, _results, _results1;
      this.width = width;
      this.height = height;
      this.container = new PIXI.DisplayObjectContainer();
      cells_yx = (function() {
        _results = [];
        for (var _i = 0, _ref = this.height; 0 <= _ref ? _i < _ref : _i > _ref; 0 <= _ref ? _i++ : _i--){ _results.push(_i); }
        return _results;
      }).apply(this).map((function(_this) {
        return function(y) {
          var _i, _ref, _results;
          return (function() {
            _results = [];
            for (var _i = 0, _ref = _this.width; 0 <= _ref ? _i < _ref : _i > _ref; 0 <= _ref ? _i++ : _i--){ _results.push(_i); }
            return _results;
          }).apply(this).map(function(x) {
            var center, type;
            center = new PIXI.Point((x + (y % 2 === 0 ? 0.5 : 1)) * CELL_WIDTH, (y + 0.5) * ROW_STEP);
            type = CELL_TYPES[Math.floor(Math.random() * CELL_TYPES.length)];
            return new window.Cell(x, y, center, type, _this.container);
          });
        };
      })(this));
      this.cells = (function() {
        _results1 = [];
        for (var _j = 0, _ref1 = this.width; 0 <= _ref1 ? _j < _ref1 : _j > _ref1; 0 <= _ref1 ? _j++ : _j--){ _results1.push(_j); }
        return _results1;
      }).apply(this).map((function(_this) {
        return function(x) {
          var _j, _ref1, _results1;
          return (function() {
            _results1 = [];
            for (var _j = 0, _ref1 = _this.height; 0 <= _ref1 ? _j < _ref1 : _j > _ref1; 0 <= _ref1 ? _j++ : _j--){ _results1.push(_j); }
            return _results1;
          }).apply(this).map(function(y) {
            return cells_yx[y][x];
          });
        };
      })(this));
    }

    return HexGrid;

  })();

}).call(this);

//# sourceMappingURL=hexGrid.js.map
