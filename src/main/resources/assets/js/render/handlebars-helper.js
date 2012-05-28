Handlebars.registerHelper('formatDate', function(date) {
//	if (format == null) {
//		format = 'YYYY-MM-DD'
//	}
	return moment(new Date(date)).format('MMM DD, YYYY')
});

Handlebars.registerPartial('header', readFile("src/main/resources/views/js/header.hbr"))
Handlebars.registerPartial('footer', readFile("src/main/resources/views/js/footer.hbr"))
Handlebars.registerPartial('article', readFile("src/main/resources/views/js/article.hbr"))
