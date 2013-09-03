  /*jslint unparam: true, regexp: true */
/*global window, $ */
$(function () {
    'use strict';
    // Change this to the location of your server-side upload handler:
    var url = '/deploy-interactive',
    uploadButton = $('<button/>')
    .addClass('btn btn-primary')
    .prop('disabled', true)
    .text('Processing...')
    .on('click', function () {
	var $this = $(this),
	data = $this.data();
	$this
	.off('click')
	.text('Abort')
	.on('click', function () {
	  $this.remove();
	  data.abort();
	  });
	data.submit().always(function () {
	  $this.remove();
	  });
	});
$('#fileupload').fileupload({ url: url, dataType: 'json', autoUpload: false, acceptFileTypes: /(\.|\/)(zip)$/i,
    disableImageResize: /Android(?!.*Chrome)|Opera/ .test(window.navigator.userAgent), previewMaxWidth: 100,
    previewMaxHeight: 100, previewCrop: true }).on('fileuploadadd', function (e, data) {
	data.context = $('<div/>').appendTo('#files');
	$.each(data.files, function (index, file) {
	  var node = $('<p/>')
	  .append($('<span/>').text(file.name));
	  if (!index) {
	  node
	  .append('<br>')
	  .append(uploadButton.clone(true).data(data));
	  }
	  node.appendTo(data.context);
	  });
	}).on('fileuploadprocessalways', function (e, data) {
	  var index = data.index,
	  file = data.files[index],
	  node = $(data.context.children()[index]);
	  if (file.preview) {
	  node
	  .prepend('<br>')
	  .prepend(file.preview);
	  }
	  if (file.error) {
	  node
	  .append('<br>')
	  .append(file.error);
	  }
	  if (index + 1 === data.files.length) {
	  data.context.find('button')
	  .text('Upload')
	  .prop('disabled', !!data.files.error);
	  }
	  }).on('fileuploadprogressall', function (e, data) {
	    var progress = parseInt(data.loaded / data.total * 100, 10);
	    $('#progress .progress-bar').css(
		'width',
		progress + '%'
		);
	    }).on('fileuploaddone', function (e, data) {
		$.each(data.result.files, function (index, file) {
		  var link = $('<a>')
		  .attr('target', '_blank')
		  .prop('href', file.url);
		  $(data.context.children()[index])
		  .wrap(link);
		  });
		}).on('fileuploadfail', function (e, data) {
		  $.each(data.result.files, function (index, file) {
		    var error = $('<span/>').text(file.error);
		    $(data.context.children()[index])
		    .append('<br>')
		    .append(error);
		    });
		  }).prop('disabled', !$.support.fileInput).parent().addClass($.support.fileInput ? undefined : 'disabled'); });

// setting active nav item
$(document).ready(function () {
  $('.active').removeClass('active');
  var url = window.location;
  $('ul.nav a').filter(function () {
	return this.href == url;
   }).parent().addClass('active').parent().parent().addClass('active');
});
