<#import "spring.ftl" as spring />

<html>

  <head>
    <title>${command.title}</title>
    <meta name="pageName" content="${command.name}">
    <meta name="moduleName" content="editable-pages">
    <script type='text/javascript'
      src="${baseweb}/module/ajax/fckeditor/fckeditor.js">
    </script>

    <script type="text/javascript">
      window.onload = function() {
        var editor = new FCKeditor('pageContent');

        editor.Config['EditorAreaCSS'] = '${baseweb
          }${fckEditorConfiguration.editorAreaCss!
            "/module/decorator/css/fck-editorarea.css"}';

        editor.BasePath = '${baseweb}/module/ajax/fckeditor/';

        editor.Config['CustomConfigurationsPath'] = '${baseweb
          }${fckEditorConfiguration.configurationUrl!
            "/module/editable-pages/asset/js/fckconfig.js"}';

        editor.ToolbarSet = '${fckEditorConfiguration.toolbarSet!
          "EditablePagesMain"}';

        <#if fckEditorConfiguration.height??>
          editor.Height = '${fckEditorConfiguration.height}';
        </#if>

        <#if fckEditorConfiguration.width??>
          editor.Width = '${fckEditorConfiguration.width}';
        </#if>

        editor.ReplaceTextarea();
      }
    </script>
    <script type="text/javascript">
    function onCancel(){
      if(confirm("Are you sure you want to discard your changes?")){
        window.location='${request.contextPath}/page/${command.originalName}';
      }
    }
    </script>

  </head>

  <body>
    <h3>Edit Page</h3>

    <form id="userEdit" name="userEdit" method="POST" action="edit.do">

      <#assign htmlEscape = true in spring>

      <span class="error" id="message">
        <@spring.bind "command.*"/>
        <@spring.showErrors "<br/>" />
      </span>

      <@spring.formHiddenInput "command.id" />

      <span class="formfield">
        <!-- label for="name">Name:</label  -->
        <@spring.formHiddenInput "command.name" />
      </span>

      <span class="formfield">
        <label for="title">Title:</label>
        <@spring.formInput "command.title", "maxlength=50"/>
      </span>

      <span class="formfield">
        <@spring.formTextarea "command.pageContent", "cols='160' rows='160'" />
      </span>

      <input class='btn' type="submit" value="Save"/>
      <input class='btn' type="button" value="Discard" onclick="onCancel();"/>

    </form>
  </body>
</html>

<!-- vim: set ts=2 et sw=2 ai ft=xml: -->

