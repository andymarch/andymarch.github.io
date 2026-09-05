---
layout: page
title: Archive
permalink: /archive/
---

The full, unfiltered history of this site, newest first.

{% assign all_posts = site.posts | sort: "date" | reverse %}
<div class="table-wrap">
<table class="archive-table">
  <thead>
    <tr>
      <th>Date</th>
      <th>Type</th>
      <th>Title</th>
    </tr>
  </thead>
  <tbody>
    {%- for post in all_posts -%}
    <tr>
      <td>{{ post.date | date: "%b %-d, %Y" }}</td>
      <td>{{ post.type | default: "post" }}</td>
      <td><a href="{{ post.url | relative_url }}">{{ post.title | default: post.excerpt | strip_html | truncate: 60 }}</a></td>
    </tr>
    {%- endfor -%}
  </tbody>
</table>
</div>

Also see [ColonyFS](/colonyfs/), a static archived project page from 2009 that predates this Jekyll site.
