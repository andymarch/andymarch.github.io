---
layout: page
title: Speaking
permalink: /speaking/
---

# Speaker Bio

> Andy March is an engineering manager at Okta, the leading independent provider of identity for the enterprise, where he leads a distributed presales development team of four engineers helping the world's largest organizations design and build customer identity solutions.
>
> Before moving into engineering leadership he spent over a decade writing secure, intuitive software himself — everything from fighter planes and ATMs to e-passports and customer loyalty programs. He's a firm believer in code winning arguments and the compounding awesomeness of fixing small things, and still geeks out on security, developer tools, and a decent cup of coffee.
    
# Photo

![Image of Andy
March](http://gravatar.com/avatar/d7c78f8757327b65a637aece98939f01?s=200)

The above image can be scaled to your requirements with the following url:

http://gravatar.com/avatar/d7c78f8757327b65a637aece98939f01?s=<your preferred size>.

For example:

http://gravatar.com/avatar/d7c78f8757327b65a637aece98939f01?s=100

http://gravatar.com/avatar/d7c78f8757327b65a637aece98939f01?s=250

An uncompressed version can be found [here](/assets/img/andymarch-speakerheadshot-uncompressed.jpg).

# Talks

All slide decks are also published on [Speaker Deck](https://speakerdeck.com/andymarch).

{% assign sorted_talks = site.data.talks | sort: "date" | reverse %}
<div class="table-wrap">
<table class="talks-table">
  <thead>
    <tr>
      <th>Date</th>
      <th>Event</th>
      <th>Talk</th>
      <th>Links</th>
    </tr>
  </thead>
  <tbody>
    {%- for talk in sorted_talks -%}
    <tr>
      <td>{{ talk.date | date: "%b %Y" }}</td>
      <td>{{ talk.event }}</td>
      <td>{{ talk.title }}</td>
      <td>
        {%- if talk.deck_url -%}<a href="{{ talk.deck_url }}">Slides</a>{%- endif -%}
        {%- if talk.deck_url and talk.video_url %} &middot; {% endif -%}
        {%- if talk.video_url -%}<a href="{{ talk.video_url }}">Video</a>{%- endif -%}
      </td>
    </tr>
    {%- endfor -%}
  </tbody>
</table>
</div>

Older talks and conferences attended are covered on the [home page]({{ "/" | relative_url }}).