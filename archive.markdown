---
layout: page
title: Archive
permalink: /archive/
---

Every post on this site, newest first — start typing to filter by title, type, or year.

<input type="text" id="archive-filter" class="archive-filter" placeholder="Filter…" autocomplete="off">

<ul class="archive-list" id="archive-list">
{%- assign all_posts = site.posts | sort: "date" | reverse -%}
{%- for post in all_posts -%}
{%- assign post_title = post.title | default: post.excerpt | strip_html | truncate: 80 -%}
{%- assign post_type = post.type | default: "post" -%}
{%- assign post_year = post.date | date: "%Y" -%}
{%- assign post_search = post_title | append: " " | append: post_type | append: " " | append: post_year | downcase -%}
<li class="archive-row" data-search="{{ post_search }}">
  <span class="archive-date">{{ post.date | date: "%b %-d, %Y" }}</span>
  <span class="archive-type">{{ post_type }}</span>
  <a class="archive-title" href="{{ post.url | relative_url }}">{{ post_title }}</a>
</li>
{%- endfor -%}
</ul>

<p class="archive-empty" id="archive-empty" hidden>Nothing matches that.</p>

Also see [ColonyFS](/colonyfs/), a static archived project page from 2009 that predates this Jekyll site.

<script src="{{ '/assets/js/archive-filter.js' | relative_url }}" defer></script>
