<#include "header.ftl" />

<div class="row editArticle" style="display: none">

<h2>Edit article</h2>

<form class="well" id="editArticleForm">

<label>Title</label>
<input type="text" name="title">

<label>Slug</label>
<input type="text" name="slug">
<input type="hidden" name="oldSlug">

<label>Tags</label>
<input type="text" name="tags">

<label>Date</label>
<input type="text" name="dateCreated">

<label>Text</label>
<textarea name="text"></textarea>

<div>
<button class="btn btn-success saveArticle">Save Post</button>
</div>

</form>
</div> 

<div class="row">

<div class="span2">
<button class="btn btn-success createArticle">New Post</button>
</div>

<div class="span8">

<table class="table table-striped table-condensed">
<#list articles as article>
  <tr class="view_${article.slug}">
    <td><a href="/article/${article.slug}">${article.title}</a> at <#if article.dateCreated??>${article.dateCreated?date}</#if></td>
    <td><a class="btn btn-primary btn-mini editArticleLink" data-slug="${article.slug}">Edit Post</a></td>
    <td><a class="btn btn-danger btn-mini deleteLink" data-slug="${article.slug}">Delete Post</a><a class="btn btn-danger btn-mini deleteLinkConfirm" data-slug="${article.slug}" style="display: none">Really!</a></td>
  </tr> 
</#list>  
</table>

</div>

</div>

<#include "footer.ftl" />
