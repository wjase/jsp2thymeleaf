
import com.cybernostics.jsp2thymeleaf.api.expressions.function.FunctionConverterSource

//import com.cybernostics.jsp2thymeleaf.api

class  JstlFormatConverterSource extends FunctionConverterSource{
    
    JstlFormatConverterSource (){
        super("http://java.sun.com/jsp/jstl/functions",converters())
    }
    
    
    static def converters(){
        def converterList = []

        //        contains( java.lang.String, java.lang.String)
        //<c:if test="${fn:containsIgnoreCase(name, searchString)}">
        //        containsIgnoreCase( java.lang.String, java.lang.String)
        //        endsWith( java.lang.String, java.lang.String)
        //        escapeXml( java.lang.String)
        //        indexOf( java.lang.String, java.lang.String)
        //        join( java.lang.String[], java.lang.String)
        //        length( java.lang.Object)
        //        replace( java.lang.String, java.lang.String, java.lang.String)
        //        split( java.lang.String, java.lang.String)
        //        startsWith( java.lang.String, java.lang.String)
        //        substring( java.lang.String, int, int)
        //        substringAfter( java.lang.String, java.lang.String)
        //        substringBefore( java.lang.String, java.lang.String)
        //        toLowerCase( java.lang.String)
        //        toUpperCase( java.lang.String)
        //        trim( java.lang.String)
    
    }    

}

