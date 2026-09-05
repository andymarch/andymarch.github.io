---
layout: page
title: Reading
permalink: /reading/
---

Notes and highlights from books I've read, newest first.

{% assign book_notes = site.posts | where: "type", "book-note" | sort: "date" | reverse %}
<div class="table-wrap">
<table class="reading-table">
  <thead>
    <tr>
      <th>Date</th>
      <th>Book</th>
    </tr>
  </thead>
  <tbody>
    {%- for post in book_notes -%}
    <tr>
      <td>{{ post.date | date: "%b %Y" }}</td>
      <td><a href="{{ post.url | relative_url }}">{{ post.title }}</a></td>
    </tr>
    {%- endfor -%}
  </tbody>
</table>
</div>
