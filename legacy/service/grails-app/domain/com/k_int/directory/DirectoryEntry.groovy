package com.k_int.directory

import com.k_int.CanEdit;
import com.k_int.ill.itemSearch.SearchGroup;
import com.k_int.ill.referenceData.RefdataValueData;
import com.k_int.web.toolkit.custprops.CustomProperties;
import com.k_int.web.toolkit.databinding.BindUsingWhenRef;
import com.k_int.web.toolkit.refdata.CategoryId;
import com.k_int.web.toolkit.refdata.Defaults;
import com.k_int.web.toolkit.refdata.RefdataValue;
import com.k_int.web.toolkit.tags.Tag;

import grails.gorm.MultiTenant;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;

// Called when data binding wants to bind a variable of type DirectoryEntry to any domain
// class. obj will be an instance of that class, propName will be the property name which has
// type DirectoryEntry and source will be the source map.
@BindUsingWhenRef({ obj, propName, source, isCollection = false ->
  CustomBinders.bindDirectoryEntry(obj, propName, source, isCollection)
})
@ExcludeFromGeneratedCoverageReport
class DirectoryEntry  implements MultiTenant<DirectoryEntry>,CustomProperties  {

  String id
  String name
  String slug
  String description
  String foafUrl
  String entryUrl
  String phoneNumber
  String emailAddress
  String contactName

  // URL of an SVG file used for branding relating to this directory entry
  String brandingUrl

  // This field is used to store a parsed version of the last modified time as harvested
  // from a remote server. It can be used to work out if a remote system thinks that
  // the directory entry has changed since the last time we visited. Its used to make it
  // more efficient for us to regularly poll the network of directory entries and be able to
  // quickly discard records that have not changed. The naming is slightly clunky to avoid
  // confusing with the lastUpdate field that could be applied to _this_ record.
  Long pubLastUpdate

  @Defaults(['Consortium', 'Institution', 'Branch', 'Faculty', 'Department', 'Organisation'])
  RefdataValue type;

  // If the location corresponds to a location in a host LMS, record that
  // code here - "MAIN" is a common example
  String lmsLocationCode

  Long foafTimestamp

  DirectoryEntry parent

  // The search directory groups
  DirectoryGroups searchDirectoryGroups;
  
  // The searches to be performed
  SearchGroup searches;
  
  // The host LMS type, this will be used for interpreting the holdings and availability
  @CategoryId(RefdataValueData.VOCABULARY_HOST_LMS_INTEGRATION_ADAPTER)
  RefdataValue hostLmsType;
  
//  @BindImmutably
  Set<Tag> tags = []

  /**
   * DirectoryEntries can be managed here, or just stored for reference. Managed entries can be accessed via
   * a foaf type service.
   */
  @Defaults(['Managed', 'Reference' ])
  RefdataValue status

  /** Signifies whether the current user can edit this record or not, this is not persisted */
  CanEdit canEdit;

  static graphql = true

  // The host lms can be defined on a parent
  String determinedHostLms;
  
  static transients = [
      'canEdit',
	  'determinedHostLms',
      'firstSymbol'
  ];

  static hasMany = [
    tags:Tag,
    friends: FriendAssertion,
    units: DirectoryEntry,
    symbols: Symbol,
    services: ServiceAccount,
    announcements: Announcement,
    addresses: Address,
    members: GroupMember
  ]

  static mappedBy = [
    friends: 'owner',
    units: 'parent',
    symbols: 'owner',
    services: 'accountHolder',
    announcements: 'owner',
    addresses: 'owner',
    members: 'groupOrg'
  ]

  static belongsTo = [
    parent: DirectoryEntry
  ]

  static mapping = {
                       id column: 'de_id', generator: 'assigned', length:36
                     name column: 'de_name'
                     slug column: 'de_slug'
              description column: 'de_desc'
                   parent column: 'de_parent', cascade:'save-update'
                   status column: 'de_status_fk'
                  foafUrl column: 'de_foaf_url'
                 entryUrl column: 'de_entry_url'
              brandingUrl column: 'de_branding_url'
            foafTimestamp column: 'de_foaf_timestamp'
          lmsLocationCode column: 'de_lms_location_code'
              phoneNumber column: 'de_phone_number'
             emailAddress column: 'de_email_address'
              contactName column: 'de_contact_name'
                     type column: 'de_type_rv_fk'
            pubLastUpdate column: 'de_published_last_update'
	searchDirectoryGroups column: 'de_search_directory_groups'
	             searches column: 'de_searches'
		      hostLmsType column: 'de_host_lms_type'
                     tags cascade:'save-update'
                    units cascade:'save-update'
                 services cascade:'all-delete-orphan'
                addresses cascade:'all-delete-orphan'
                  members cascade:'all-delete-orphan'
                  symbols cascade:'all-delete-orphan'
                  friends cascade:'all-delete-orphan'
            announcements cascade:'all-delete-orphan'
  }

  static constraints = {
                       id(bindable:true)
                     name(nullable:false, blank:false)
                     slug(nullable:false, blank:false, unique:true)
              description(nullable:true, blank:false)
                   parent(nullable:true, validator: { parent, obj ->
                    return obj.checkParentTree(obj, parent)
                  })
                   status(nullable:true)
                  foafUrl(nullable:true, blank:false)
                 entryUrl(nullable:true, blank:false)
              brandingUrl(nullable:true, blank:false)
            foafTimestamp(nullable:true)
          lmsLocationCode(nullable:true)
              phoneNumber(nullable:true, blank:false)
             emailAddress(nullable:true, blank:false)
              contactName(nullable:true, blank:false)
                     type(nullable:true)
            pubLastUpdate(nullable:true)
	searchDirectoryGroups(nullable:true)
	             searches(nullable:true)
			  hostLmsType(nullable:true)
  }

    def beforeValidate() {
        // if we do not have an id allocate it here
        // We do not automatically allocate one as we may create one with a specific id
        if (id == null) {
            // Not already assigned, so assign a random uuid
            id = UUID.randomUUID().toString();
        }
    }

  // Propagate any last update data to all parent records
  public void setPubLastUpdate(Long pubLastUpdate) {
    this.pubLastUpdate = pubLastUpdate;
    if ( parent != null )
      parent.setPubLastUpdate(pubLastUpdate);
  }

  public String getFirstSymbol() {
      String result = null;
      if (symbols && (symbols.size() > 0)) {
          Symbol symbol = symbols[0];
          result = symbol.authority.symbol + ":" + symbol.symbol;
      }
      return(result);
  }

  /**
  * Search through the parent tree to find out if this directory entry appears as its own parent along the line
  * @return null if there are no parental loops, string saying "Cycle Detected" otherwise
  * @param the_leaf The end of the chain in e1 -> e1.1 -> e1.1.1 -> e1.1.1.1 this will always have the value e1.1.1.1
  * @param the_parent The current parent to check in the chain.
  */
  public String checkParentTree(DirectoryEntry the_leaf, DirectoryEntry the_parent) {
    // We can only ever introduce a cycle if we are taking an existing node and updating it's parent to point to another
    // existing node - so return right way if this is a new node
    if ((the_parent == null)||(the_parent.id==null)||(the_leaf?.id==null)) {
      // There is no parent OR the parent is new or this node is new - all is well
      return null
    } else {
      // If the ID of the parent is the same as the ID of the leaf node then we have found a cycle. Error!
      if (the_leaf.id == the_parent.id) {
        return "Cycle detected leaf_id=${the_leaf?.id} paren_id=${the_parent?.id}"
      } else {
        // No cycle found yet, check the parent of the parent (Which might be null, which is fine)
        return checkParentTree(the_leaf, the_parent.getParent())
      }
    }
  }

  /**
   * Search the symbols attached to this entry to see if we can find one in the nominated
   * namespace, if not and if there is a parent, try the parent
   */
  public String locateSymbolInNamespace(String ns) {
    String result = null;
    Symbol located_symbol = symbols.find { it.authority.symbol == ns }

    // If we managed to find a symbol in that namespace, job done, return it
    if ( located_symbol ) {
      result = located_symbol.symbol
    }
    else {
      // We didn't, does this entry have a parent?
      if ( parent ) {
        // Yes, see if the parent has a symbol matching that NS
        result = parent.locateSymbolInNamespace(ns);
      }
    }

    return result
  }

  public String getTagSummary() {
    String result = null;
    if ( ( tags ) && ( tags.size() > 0 ) )
      result = tags?.collect {it?.value?:'Missing Tag'}.join(', ')
    else
      result = 'No tags'
    return result;
  }

  public String getSymbolSummary() {
    String result = null;
    if ( ( symbols ) && ( symbols.size() > 0 ) )
      result = symbols?.collect {it?.authority?.symbol+':'+it?.symbol}.join(', ')
    else
      result = 'No symbols'
    return result;
  }

  public String getFullyQualifiedName() {
    String result = null;
    if ( parent ) {
      result = parent.getFullyQualifiedName() + ' / ' + name
    }
    else {
      result = name
    }
    return result;
  }

  public String toString() {
    return "DirectoryEntry ${name} (${id?:'Unsaved'})".toString()
  }

  public int hashCode() {
    "DirectoryEntry:${slug}".toString().hashCode()
  }
}
