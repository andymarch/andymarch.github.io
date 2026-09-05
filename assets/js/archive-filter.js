(function () {
  var input = document.getElementById('archive-filter');
  var list = document.getElementById('archive-list');
  var empty = document.getElementById('archive-empty');
  if (!input || !list) return;

  var rows = Array.prototype.slice.call(list.querySelectorAll('.archive-row'));

  input.addEventListener('input', function () {
    var query = input.value.trim().toLowerCase();
    var visible = 0;

    rows.forEach(function (row) {
      var match = row.getAttribute('data-search').indexOf(query) !== -1;
      row.hidden = !match;
      if (match) visible++;
    });

    if (empty) empty.hidden = visible !== 0;
  });
})();
