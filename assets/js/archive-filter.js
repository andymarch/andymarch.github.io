(function () {
  var input = document.getElementById('archive-filter');
  var list = document.getElementById('archive-list');
  var empty = document.getElementById('archive-empty');
  if (!input || !list) return;

  var rows = Array.prototype.slice.call(list.querySelectorAll('.archive-row'));
  var years = Array.prototype.slice.call(list.querySelectorAll('.archive-year'));

  function updateYearHeadings() {
    years.forEach(function (heading) {
      var year = heading.getAttribute('data-year-heading');
      var hasVisibleRow = rows.some(function (row) {
        return row.getAttribute('data-year') === year && !row.hidden;
      });
      heading.hidden = !hasVisibleRow;
    });
  }

  input.addEventListener('input', function () {
    var query = input.value.trim().toLowerCase();
    var visible = 0;

    rows.forEach(function (row) {
      var match = row.getAttribute('data-search').indexOf(query) !== -1;
      row.hidden = !match;
      if (match) visible++;
    });

    updateYearHeadings();

    if (empty) empty.hidden = visible !== 0;
  });
})();
