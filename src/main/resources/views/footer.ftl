    
    
    </div> <!-- span8 -->
    </div> <!-- row -->
    </div> <!-- /container -->

<div class="modal fade" id="aboutModal">
  <div class="modal-header">
    <a class="close" data-dismiss="modal">×</a>
    <h3>About me</h3>
  </div>
  <div class="modal-body">
    <p>
    This is my sample dropwizard and elasticsearch project, I hacked together for fun and profit.<br/>
    You can fork it on github and play around with it as well. Technologies used: <br />
    <ul>
      <li>Dropwizard 0.4.0</li>
      <li>Elasticsearch 0.19.3</li>
      <li>Twitter Bootstrap</li>
      <li>jQuery, jQueryUI, datetimepicker, tags input, formToJson serializer, jq tagcloud</li>
      <li>Handlebars.js, momentjs</li>
      <li>FluentLenium</li>
    </ul>
    
    </p>
  </div>
  <div class="modal-footer">
  by Alexander Reelsen
  </div>
</div>


<div class="modal fade" id="contactModal">
  <div class="modal-header">
    <a class="close" data-dismiss="modal">×</a>
    <h3>Contact</h3>
  </div>
  <div class="modal-body">
    <p>Drop me a note at <a href="http://twitter.com/spinscale">twitter</a>, <a href="http://github.com/spinscale">github</a>, 
    <a href="https://www.xing.com/profile/Alexander_Reelsen">xing</a> or even old fashioned <a href="mailto:alexander@reelsen.net">email</a>. <br />
    Questions, corrections, critics, pull requests, job offers, free beers, you-name-it are all appreciated.
    </p>
  </div>
  <div class="modal-footer">
  by Alexander Reelsen
  </div>
</div>

    <!-- Placed at the end of the document so the pages load faster -->
    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
    <script src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.18/jquery-ui.min.js"></script>
    <script src="http://cloud.github.com/downloads/wycats/handlebars.js/handlebars-1.0.0.beta.6.js"></script>
    <script src="/assets/bootstrap/js/bootstrap.min.js"></script>
    <script src="/assets/js/blog.js"></script>
    <script src="/assets/js/moment.min.js"></script>
    <script src="/assets/js/formjson.js"></script>
    <script src="/assets/js/jquery.tagsinput.min.js"></script>
    <script src="/assets/js/jquery.datetimepicker.js"></script>
    <script src="/assets/js/jquery.qp_tagcloud.js"></script>

<script id="article-template" type="text/x-handlebars-template">
<div class="row">
<h2><a href="/article/{{article.slug}}">{{article.title}}</a></h2>
<h3>by {{article.author}}, at {{formatDate article.dateCreated}}</h3>
<div>{{{article.text}}}</div>
</div>
</script>

  </body>
</html>