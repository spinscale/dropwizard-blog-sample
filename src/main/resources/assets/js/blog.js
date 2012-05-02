var lastSearch = ""
var articleTemplate

Handlebars.registerHelper('formatDate', function(date) {
//	if (format == null) {
//		format = 'YYYY-MM-DD'
//	}
	return moment(new Date(date)).format('MMM DD, YYYY')
});

// Render handlebar template from a search results
function parseSearchResults(data) {
	if (data.articles != undefined && data.articles.length > 0) {
		$('.articles').empty()
		$.each(data.articles, function(index, value) {
			var html = articleTemplate({article: value})
			$('.articles').append(html)
		})
	}
}

// Fill in the edit form in the admin frontend
function fillEditArticleForm(data) {
	$.each(data.article, function(field, value) {
		$('#editArticleForm *[name=' + field +']').val(value);
	});
	// special old slug handling
	$('#editArticleForm *[name=oldSlug]').val(data.article.slug)
	// special date handling
	$('#editArticleForm *[name=dateCreated]').val(moment(data.article.dateCreated).format('MM/DD/YYYY HH:mm:ss'))
	$('#editArticleForm input[name=tags]').importTags('');
	$.each(data.article.tags, function(index, value) {
		$('#editArticleForm input[name=tags]').addTag(value)
	})
	$('.editArticle').fadeIn('slow')
}

function fillTagCloud(data) {
	$('#tagcloud').tagcloud({
	    words: data,    // Tagcloud-Array
	    fontMin: 1,      // Minimale Schriftgrösse (hier: 0.75em)
	    fontMax: 1.5,      // Maximale Schriftgrösse (hier: 1.75em)
	    fontUnit: 'em',     // Einheit für die Schriftgrösse (hier: em)
	    animFontDiff: 0.2,  // Angabe der Schriftgrössenänderung beim Hover (hier: 0.4em)
	    animDuration: 100   // Dauer der Hover-Animation in Millisekunden (hier: 400 ms)
	  })
}

function deleteArticle(slug) {
	$.ajax({
		url: '/article/' + slug,
		type: 'DELETE',
		success: function() {
			$('.view_'+slug).remove()
		}
	})
}

$(document).ready(function() {
	var source = $('#article-template').html();
	articleTemplate = Handlebars.compile(source);
		
	// Disable return on search
	$('#search').keypress(function(e){
		if ( e.which == 13 ) return false;
	})
	
	$('#search').keyup(function(evt) {
		var term = $('#search').val()
		if (term == lastSearch) return
		
		if (term.length == 0) {
			$.getJSON('/', { term: term }, parseSearchResults)
		} else {
			$.getJSON('/search', { term: term }, parseSearchResults)
			lastSearch = term
		}
	})
	
	// Remove other articles on link click
	$('.articles h2 a').live('click', function(evt) {
		evt.preventDefault();
		var html = $(this).parent().parent() // Ugly as hell, please fix me without beer
        $('.articles').empty()
        $('.articles').append(html)
	})

	// Load on tag click
	$('#tagcloud a').live('click', function(evt) {
		evt.preventDefault();
		var tag = $(this).html()
		$.getJSON('/tags/' + tag, {}, parseSearchResults)
	})
	
	// Show confirm button on delete click
	$('.deleteLink').live('click', function(evt) { 
		evt.preventDefault()
		var slug = $(this).data('slug')
		$(this).css('display', 'none')
		$('a.deleteLinkConfirm[data-slug=' + slug + ']').css('display', 'block')
	})
	
	// Delete on click
	$('.deleteLinkConfirm').live('click', function(evt) {
		evt.preventDefault();
		var slug = $(this).data('slug')
		deleteArticle(slug)
	})
	
	// Load article on click on edit
	$('.editArticleLink').live('click', function(evt) {
		evt.preventDefault();
		var slug = $(this).data('slug')
		
		$.getJSON('/article/'+slug, fillEditArticleForm)
	})
	
	$('.saveArticle').click(function(evt) {
		evt.preventDefault();
		var jsonData = $('#editArticleForm').toJSON()
		if (jsonData.slug != jsonData.oldSlug && jsonData.oldSlug != null) {
			deleteArticle(jsonData.oldSlug)
		}
		delete jsonData.oldSlug
		// special handling for tags as array
		jsonData.tags = jsonData.tags.split(',')
		// special handling for dateCreated as date
		jsonData.dateCreated = moment(jsonData.dateCreated, 'MM/DD/YYYY HH:mm:ss').toDate()
		$.ajax({
			url: '/article/',
			type: 'PUT',
			data : JSON.stringify(jsonData),
			processData : false,
			dataType: 'JSON',
			contentType: 'application/json',
			success: function() {
				$('.editArticle').fadeOut('slow')
			}
		})
	})
	
	$('.createArticle').click(function() {
		$('.editArticle').fadeIn('slow')
	})
	
	// tags input and datepicker
	$('#editArticleForm').find('input[name=tags]').tagsInput();
	$('#editArticleForm').find('input[name=dateCreated]').datetimepicker({timeFormat: 'hh:mm:ss'});
	
	// tag cloud baby
	// Get words
	$.getJSON('/tags', fillTagCloud)
})