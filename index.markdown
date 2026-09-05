---
layout: default
permalink: /
---

**Now:** engineering manager at Okta, leading a distributed presales development team of four engineers.

{% assign curated = site.posts | where_exp: "post", "post.type == 'talk' or post.type == 'book-note' or post.type == 'journal'" | sort: "date" | reverse %}
{% assign recent = curated | slice: 0, 5 %}

<ul class="post-list">
  {%- for post in recent -%}
  <li>
    <h3>
      <a class="post-link" href="{{ post.url | relative_url }}">{{ post.title | escape }}</a>
      <span class="post-meta">{{ post.date | date: "%b %-d, %Y" }}</span>
    </h3>
  </li>
  {%- endfor -%}
</ul>

See the full history in the [archive]({{ "/archive/" | relative_url }}).
