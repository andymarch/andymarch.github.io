(function () {
  var list = document.getElementById('post-list');
  var sentinel = document.getElementById('infinite-scroll-sentinel');
  if (!list || !sentinel || !('IntersectionObserver' in window)) return;

  var loading = false;

  function loadNextPage() {
    var nextUrl = sentinel.getAttribute('data-next');
    if (!nextUrl || loading) return;
    loading = true;
    sentinel.textContent = 'Loading more posts…';

    fetch(nextUrl)
      .then(function (response) { return response.text(); })
      .then(function (html) {
        var doc = new DOMParser().parseFromString(html, 'text/html');
        var newList = doc.getElementById('post-list');
        var newSentinel = doc.getElementById('infinite-scroll-sentinel');

        if (newList) {
          while (newList.firstChild) {
            list.appendChild(newList.firstChild);
          }
        }

        var next = newSentinel && newSentinel.getAttribute('data-next');
        if (next) {
          sentinel.setAttribute('data-next', next);
          sentinel.textContent = '';
        } else {
          sentinel.removeAttribute('data-next');
          sentinel.textContent = '';
          observer.disconnect();
        }

        loading = false;
      })
      .catch(function () {
        loading = false;
        sentinel.textContent = '';
      });
  }

  var observer = new IntersectionObserver(function (entries) {
    entries.forEach(function (entry) {
      if (entry.isIntersecting) loadNextPage();
    });
  }, { rootMargin: '300px' });

  observer.observe(sentinel);
})();
