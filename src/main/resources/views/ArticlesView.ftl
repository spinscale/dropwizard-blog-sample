<#include "header.ftl" />


<div class="articles">

<#list articles as article>
<div class="row">
<h2><a href="/article/${article.slug}">${article.title}</a></h2>
<h3>by ${article.author}, at ${article.dateCreated?date}</h3>
<div>${article.text}</div>
</div>
</#list>

</div>


<#include "footer.ftl" />
