// Place your Spring DSL code here
import grails.util.Environment
import com.k_int.ill.FolioEmailServiceImpl;
import com.k_int.ill.sharedindex.jiscdiscover.JiscDiscoverApiConnectionImpl;
import com.k_int.ill.sharedindex.openRS.connections.OpenRsAvailabilityApiConnectionImpl;
import com.k_int.ill.sharedindex.openRS.connections.OpenRsSharedIndexApiConnectionImpl;
import com.k_int.ill.sharedindex.openRS.connections.OpenRsTokenApiConnectionImpl;
import com.k_int.ill.*;
import com.k_int.ill.sharedindex.jiscdiscover.*;
//import com.k_int.gorm.SqlInterceptor;

beans = {
    // No mock versions of these at the moment
//    eventEntityInterceptor(SqlInterceptor)
    openRsAvailabilityApiConnection(OpenRsAvailabilityApiConnectionImpl)
    openRsSharedIndexApiConnection(OpenRsSharedIndexApiConnectionImpl)
    openRsTokenApiConnection(OpenRsTokenApiConnectionImpl)

  switch(Environment.current) {
    case Environment.TEST:
      emailService(MockEmailServiceImpl)
      jiscDiscoverApiConnection(JiscDiscoverApiConnectionMock)
      break;
      
    default:
      emailService(FolioEmailServiceImpl)
      jiscDiscoverApiConnection(JiscDiscoverApiConnectionImpl)
      break;
  }
}
