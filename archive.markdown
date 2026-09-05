---
layout: page
title: Archive
permalink: /archive/
---

Every post on this site, newest first — start typing to filter by title, type, or year.

<input type="text" id="archive-filter" class="archive-filter" placeholder="Filter…" autocomplete="off">

<ul class="archive-list" id="archive-list">
{%- assign all_posts = site.posts | sort: "date" | reverse -%}
{%- assign current_year = "" -%}
{%- for post in all_posts -%}
{%- assign post_year = post.date | date: "%Y" -%}
{%- if post_year != current_year -%}
<li class="archive-year" data-year-heading="{{ post_year }}">{{ post_year }}</li>
{%- assign current_year = post_year -%}
{%- endif -%}
{%- assign post_title = post.title | default: post.excerpt | strip_html | truncate: 80 -%}
{%- assign post_type = post.type | default: "post" -%}
{%- assign post_search = post_title | append: " " | append: post_type | append: " " | append: post_year | downcase -%}
<li class="archive-row" data-year="{{ post_year }}" data-search="{{ post_search }}">
  <span class="archive-date">{{ post.date | date: "%b %-d, %Y" }}</span>
  <span class="archive-type">{{ post_type }}</span>
  <a class="archive-title" href="{{ post.url | relative_url }}">{{ post_title }}</a>
</li>
{%- endfor -%}
</ul>

<p class="archive-empty" id="archive-empty" hidden>Nothing matches that.</p>

<script src="{{ '/assets/js/archive-filter.js' | relative_url }}" defer></script>
