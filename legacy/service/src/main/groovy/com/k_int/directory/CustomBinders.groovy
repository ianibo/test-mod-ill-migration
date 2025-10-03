package com.k_int.directory

import grails.util.GrailsNameUtils
import grails.web.databinding.DataBindingUtils
import groovy.util.logging.Slf4j

@Slf4j
class CustomBinders {
  
  public static final bindNamingAuthority(final def obj, final String propName, final def source, final boolean isCollection) { 

    log.debug ("NamingAuthority::@BindUsingWhenRef ${obj} ${propName} ${source}")

    def data = isCollection ? source : source[propName]

    // If the data is asking for null binding then ensure we return here.
    if (data == null) {
      return null
    }

    NamingAuthority val = null

    if ( data instanceof Map ) {
      if ( data.id ) {
        val = NamingAuthority.get(data.id)
      }
      else if ( data.symbol ) {
        val = NamingAuthority.findBySymbol(data.symbol) ?: new NamingAuthority(symbol:data.symbol)
      }
    }
    else if ( data instanceof String ) {
      val = NamingAuthority.findBySymbol(data) ?: new NamingAuthority(symbol:data)
    }

    if ( val ) {
      if ( data instanceof Map ) {
        DataBindingUtils.bindObjectToInstance(val, data)
      }
    }

    if ( val == null ) {
      log.warn("VAL NULL at end of data binding...");
    }

    val
  }
  
  public static final bindSymbol ( final def obj, final String propName, final def source, final boolean isCollection ) {
  
    log.debug ("Symbol::@BindUsingWhenRef ${obj} ${propName} ${source} ${isCollection}")
  
    def data = isCollection ? source : source[propName]
  
    // If the data is asking for null binding then ensure we return here.
    if (data == null) {
      return null
    }
  
    Symbol val = null

    try {
      if ( data instanceof Map ) {
        log.debug("SYMBOL Processing map of data ${data}");
        String authority_symbol = null;

        if ( data.authority instanceof String )
          authority_symbol = data.authority
        else if ( data.authority?.symbol != null ) 
          authority_symbol = data.authority.symbol

        if ( data.id ) {
          val = Symbol.get(data.id)
        }
        else if ( ( data.symbol != null ) && ( data.authority != null ) ) {
          log.debug("processing by symbol and authority ${data}");
  
          def qr = null;
  
          if ( authority_symbol != null ) {
            qr = Symbol.executeQuery('select s from Symbol as s where s.symbol=:s and s.authority.symbol=:a',[s:data.symbol, a:authority_symbol])
          }
          else {
            log.warn("Insufficient data to match symbol");
          }
    
          log.debug("Query result ${qr}");

          if ( qr?.size() == 1 ) {
            log.debug("located symbol by value");
            val = qr.get(0)
          }
          else {
            log.debug ("Create new symbol entry, ${data} - prop=${propName}, source=${source}, source.id=${source?.id}")
            // val = new Symbol(data)
            val = new Symbol()
          }
        }

      }

      if ( val ) {
        log.debug("Binding data(${data}) to symbol instance(${val})");
        DataBindingUtils.bindObjectToInstance(val, data)
    
        // If we're binding in a colleciton property, only add the symbol if it's not already in the list
        if ( isCollection ) {
          log.debug("${propName} is a collection - add ${val} to it");

          // Check that this symbol is not already present
          if ( obj[propName].find { ( ( it.symbol == val.symbol ) && ( it.authority.symbol == val.authority.symbol ) ) } == null ) {
            log.debug("Can't locate a symbol ${data.symbol} in list for ${propName} Add it - class is ${obj[propName]?.class?.name}");
            obj."addTo${GrailsNameUtils.getClassName(propName)}" (val)
          }
          else {
            log.debug("found existing symbol - no action needed")
          }
        }
        else {
          log.warn("Unhandled type ${data?.class?.name} binding Symbol");
        }
      }
      else {
        log.warn("VAL NULL at end of data binding...");
      }
    }
    catch ( Exception e ) {
      log.error("problem trying to bind symbol. rethrowing",e);
      throw e;
    }

  
    val
  }
   
  public static final bindServiceAccount(final def obj, final String propName, final def source, final boolean isCollection) {

    log.debug ("ServiceAccount::@BindUsingWhenRef ${obj} ${propName} ${source} ${isCollection}")


    def data = isCollection ? source : source[propName]

    // If the data is asking for null binding then ensure we return here.
    if (data == null) {
      return null
    }
    
    ServiceAccount val = null
    if ( data instanceof Map ) {
      if ( data.id ) {
        val = ServiceAccount.get(data.id)
      }
      else if ( data.slug ) {
        log.debug ("Lookup existing service account by slug ${data.slug}")
        val = ServiceAccount.findBySlug(data.slug)
        if ( val == null ) {
          log.debug ("Create new SA ${data} obj:${obj} id:${source?.id}...")
          // Create the account, but use cascade saving so that we get the ID of the parent
          // val = new ServiceAccount(data)

          // Try recursively calling the bind here to do the right thing
          val = new ServiceAccount(slug:data.slug)
        }
      }
    }

    if ( val != null ) {
      log.debug ("Bind ServiceAccount properties using data ${val} ${data}")

      // We're going to try ONLY binding the service to see if thats what is causing the problem - this still gives us a NULL service
      // def dbr = DataBindingUtils.bindObjectToInstance(val, data, ['service'],Collections.emptyList(), null)

      def dbr = DataBindingUtils.bindObjectToInstance(val, data)

      log.debug ("Check value of service property After bind: ${val?.service} (owner=${val.accountHolder})- should not be null if ${data.service} is present");

      // Finally - add the new service account into any colleciton property - it might be important to do this here
      // So that the object does not participate in any cascading saves until after all the properties are properly bound - in particular
      // service.
      if ( isCollection ) {
        log.debug ("${propName} is a collection - add the new object to the list")
        obj."addTo${GrailsNameUtils.getClassName(propName)}" (val)
      }
    }
    else {
      log.warn ("-- val is null, can't merge ${data}")
    }

    val
  }

  public static final bindDirectoryEntry (final def obj, final String propName, final def source, final boolean isCollection) {
    
    log.debug ("DirectoryEntry::@BindUsingWhenRef(${obj} ${source} ${propName} ${isCollection})")

    // this isn't right when we a processing a property which is a collection
    def data = isCollection ? source : source[propName]

    // If the data is asking for null binding then ensure we return here.
    if (data == null) {
      log.debug("bindDirectoryEntry called with null date - return");
      return null
    }
    else {
      log.debug("Process data ${data}");
    }

    DirectoryEntry val = null

    try {
      if ( data instanceof Map ) {
        if ( data.id ) {
          log.debug ("MAP ID supplied for Directory entry - read it")
          val = DirectoryEntry.get(data.id)
          if ( val == null ) {
            log.warn("It's this horrible case - binding data contains an ID, but we weren't able to look up that ID - create a new DE with that ID");
            // This is a weird ass case.. We've been passed a record with an ID, but weren't able to
            // look it up. We're going to create a new record for that ID - because DirectoryEntry now uses 
            // Assigned identifiers - this is fugly
            val = new DirectoryEntry();
            val.id = data.id;
            if ( isCollection ) {
              log.debug ("${propName} is a collection - so add new directory entry to parent collection")
              obj."addTo${GrailsNameUtils.getClassName(propName)}" (val)
            }
          }
        }
        else if ( data.slug != null ) {
          log.debug ("MAP Looking up directory entry by slug ${data.slug}")
          val = DirectoryEntry.findBySlug(data.slug)
          if ( val == null ) {
            log.debug ("Create new directory entry, ${data} - prop=${propName}, source=${source}");
            val = new DirectoryEntry(name:data.name, slug:data.slug)
            val.id = java.util.UUID.randomUUID().toString()

            if ( isCollection ) {
              log.debug ("${propName} is a collection - so add new directory entry to parent collection")
              obj."addTo${GrailsNameUtils.getClassName(propName)}" (val)
            }
          }
          else {
            log.debug("Located a directory entry for slug ${data.slug}(${val.id})");
          }
        }
        else {
          log.warn("DirectoryEntry binder passed a map but no usable properties");
        }

        if ( val ) {
          log.debug("Binding data to instance ${val}");
          DataBindingUtils.bindObjectToInstance(val, data)
        }
      }
      else if ( data instanceof String ) {
        log.debug("STR Data is instanceof ${data?.class?.name} so try to look up a directory entry by slug: ${data}")

        // If we're in here, someone has referenced a directory entry using only the SLUG as a string. See if we can locate using that slug
        val = DirectoryEntry.findBySlug(data)
        if ( val == null ) {

          log.debug("Creating a new placeholder directory entry for slug ${data}");

          // A bit contentious - lets create a stub entry - maybe we're loading a consortial record and we want a placeholder
          // for a member library
          val = new DirectoryEntry(slug:data, name:data)
          val.id = java.util.UUID.randomUUID().toString()
        }
      }
      else if ( data instanceof DirectoryEntry ) {
        log.warn("DM Data is instanceof DirectoryEntry - data:${data} for obj:${obj} prop is ${propName}");
        val = data;
      }
      else {
        log.error("Unhandled type ${data?.class?.name} for DirectoryEntry binding");
      }
    }
    catch (Exception e) {
      // log and rethrow
      log.error("**** unexpected error trying to process DirectryEntry ****",e);
      throw e;
    }

    if ( val == null ) {
      log.warn("bind DirectoryEntry returning NULL");
    }

    log.debug ("DirectoryEntry::@BindUsingWhenRef completed, returning ${val}")
    val
  }

  public static final bindGroupMember (final def obj, final String propName, final def source, final boolean isCollection) {

    GroupMember val = null;

    log.debug ("GroupMember::@BindUsingWhenRef(${obj} ${source} ${propName} ${isCollection})")

    // this isn't right when we a processing a property which is a collection
    def data = isCollection ? source : source[propName]

    // If the data is asking for null binding then ensure we return here.
    if (data == null) {
      return null
    }

    if ( data instanceof Map ) {
      if ( data.id ) {
        val = GroupMember.get(data.id)
      }
      else if ( ( data.memberOrg != null ) && ( data.memberOrg instanceof String ) ) {
        log.debug("data.memberOrg != null (${data.memberOrg}) and it's a string..... so do special processing");
        // Look to see if we've already gt
        if ( isCollection ) {
          log.debug("processing group member, adding to a collection (${propName}) - see if we already have an entry for that");
          val = obj[propName]?.find { it.memberOrg?.slug == data.memberOrg }

          if ( val == null ) {
            log.debug("Create a new group member for ${data}");
            // Create a new group member, and rely on bindObjectToInstance to look up the member org and set it appropriately
            val = new GroupMember()

            // Add the member org to the parent
            if ( isCollection ) {
              obj."addTo${GrailsNameUtils.getClassName(propName)}" (val)
            }
          }
        }
      }
    }
    else {
      log.debug("Data is instanceof ${data?.class?.name} - skip")
    }

    if ( ( val ) && ( data instanceof Map) ) {
      DataBindingUtils.bindObjectToInstance(val, data)
    }
    else {
      log.warn("bind DirectoryEntry returning NULL");
    }

    log.debug ("GroupMember::@BindUsingWhenRef completed, returning ${val}")

    val;
  }

  public static final bindService(final def obj, final String propName, final def source, final boolean isCollection) { 
    log.debug("Service::@BindUsingWhenRef ${obj} ${propName} ${source} ${isCollection}");

    Service val = null;

    def data = isCollection ? source : source[propName]

    // If the data is asking for null binding then ensure we return here.
    if (data == null) {
      return null
    }

    if ( data instanceof Map ) {
      if ( data.id ) {
        val = Service.get(data.id);
      }
      else if ( data.address ) {
        val = Service.findByAddress(data.address) ?: new Service(address:data.address)
      }
    }

    // Now allow binding of the properties set for that directory entry
    if (val) {
      if (data instanceof Map ) {
        DataBindingUtils.bindObjectToInstance(val, data)
      }
    }

    log.debug("Service::@BindUsingWhenRef returning ${val}");

    val
  }

}
