
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>example-app.war</title>
    <link href="../../resources/css/bootstrap.min.css" rel="stylesheet">
    <link href="../../resources/css/windup.css" rel="stylesheet" media="screen">
  </head>
  <body role="document">
    
    <!-- Fixed navbar -->
    <div class="navbar-fixed-top windup-bar" role="navigation">
      <div class="container theme-showcase" role="main">
        <img src="../../resources/img/windup-logo.png" class="logo"/>
      </div>
    </div>



    <div class="container" role="main">
        <div class="row">
          <div class="page-header page-header-no-border">
            <h1>Spring Report <span class="slash">/</span><small style="margin-left: 20px; font-weight: 100;">example-app.war</small></h1>
             <div class="navbar navbar-default">
            <div class="navbar-header">
              <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-responsive-collapse">
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
              </button>
            </div>
            <div class="navbar-collapse collapse navbar-responsive-collapse">
              <ul class="nav navbar-nav">
                <li><a href="index.html">Application</a></li>
                <li><a href="ejbs.html">EJBs</a></li>
                <li><a href="hibernate.html">Hibernate</a></li>
                <li><a href="spring.html">Spring</a></li>
                <li><a href="server-resources.html">Server Resources</a></li>
              </ul>
            </div><!-- /.nav-collapse -->
            </div>
          </div>
        </div>
       
    </div>






    <div class="container theme-showcase" role="main">
	
	<div class="panel panel-primary">
		<div class="panel-heading">
		    <h3 class="panel-title">Spring Beans</h3>
		</div>
		<div class="panel-body">
			<div class="well well-sm" style="height: 300px;">Graph of Spring Beans</div>
			<#if spring.springBeans?has_content>
			<table class="table table-striped table-bordered">
			  <tr>
			    <th>Bean Name</th><th>Java Class</th>
			  </tr>
			 <#list spring.springBeans as springBean>
			  <tr>
			    <td>${springBean.beanName}</td>
			    <td>${springBean.qualifiedName}</td>
			  </tr>
			  </#list>
			</table>
			</#if>
		</div><!--end of panel-body-->
	</div>
	
    <div class="panel panel-primary">
        <div class="panel-heading">
            <h3 class="panel-title">Spring Resources</h3>
        </div>
        <div class="panel-body">
			<div class="well well-sm" style="height: 300px;">Graph of Spring Beans</div>
			<table class="table table-striped table-bordered">
	          <tr>
	            <th>Bean Name</th><th>Java Class</th><th>Resource</th>
	          </tr>
	          <#list spring.springResourceBeans as springBean>
	          <tr>
	            <td>${springBean.beanName}</td>
	            <td>${springBean.qualifiedName}</td>
	            <td>${springBean.resourceType}</td>
	          </tr>
	          </#list>
	        </table>
		</div><!--end of panel-body-->
	</div>
	
	
	<div class="panel panel-primary">
        <div class="panel-heading">
            <h3 class="panel-title">Spring JNDI References</h3>
        </div>
        <div class="panel-body">
			<div class="well well-sm" style="height: 300px;">Graph of Spring Beans</div>
			<table class="table table-striped table-bordered">
	          <tr>
	            <th>Bean Name</th><th>Java Class</th><th>JNDI</th>
	          </tr>
	          <#list spring.springJNDIBeans as springBean>
	          <tr>
	            <td>${springBean.beanName}</td>
	            <td>${springBean.qualifiedName}</td>
	            <td>${springBean.jndiName}</td>
	          </tr>
	          </#list>
	        </table>
		</div><!--end of panel-body-->
	</div>
	        
        
        
    </div> <!-- /container -->


    <script src="https://code.jquery.com/jquery.js"></script>
    <script src="../../resources/js/bootstrap.min.js"></script>
  </body>
</html>