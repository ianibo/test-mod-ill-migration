:load ./modrsCli.groovy
okapi=new OkapiClient('reshare')
rsclient = new RSClient(okapi);

if ( 1==1 ) {
  okapi.addTenantSymbol('OCLC:ZMU');
  okapi.addTenantSymbol('OCLC:PPU');
  okapi.addTenantSymbol('ILL:LOCALSYMBOL01');
  okapi.addTenantSymbol('ILL:KNOWINT');
  okapi.addTenantSymbol('ILL:DIKUA')
  okapi.addTenantSymbol('ILL:DIKUB')
  okapi.addTenantSymbol('ILL:DIKUC')
  okapi.addTenantSymbol('ILL:KINT')
  okapi.addTenantSymbol('ILL:KNOWINT01')
  okapi.addTenantSymbol('ILL:TESTINST01')
  okapi.addTenantSymbol('ILL:TESTINST02')
  okapi.addTenantSymbol('ILL:TESTINST03')
  okapi.addTenantSymbol('ILL:TESTINST04')
  okapi.addTenantSymbol('ILL:TESTINST05')
  okapi.addTenantSymbol('ILL:TESTINST06')
  okapi.addTenantSymbol('ILL:TESTINST07')
  okapi.addTenantSymbol('ILL:TESTINST08')
  okapi.addTenantSymbol('ILL:TESTINST09')
  okapi.addTenantSymbol('ILL:TESTINST10')
  okapi.addTenantSymbol('ILL:IDVUFIND')
  okapi.addTenantSymbol('ILL:VLA')
  okapi.addTenantSymbol('ILL:MVS')
  okapi.addTenantSymbol('ILL:TEU')
}

if ( 1==1 ) {
  // okapi.walkFoafGraph()
  println("Symbols....");
  okapi.listTenantSymbols().each {  it ->
    println(it.toString());
  }
}

if ( 1==1 ) {
  // okapi.createRequest([
  //                      title:'The Heart of Enterprise',
  //                      patronIdentifier: 'PI',
  //                      patronReference: 'PR',
  //                      patronSurname: 'PS',
  //                      patronGivenName: 'PGN',
  //                      patronType:'PT',
  //                      requestingInstitutionSymbol:'ILL:KNOWINT']);

  //okapi.createRequest([title:'The Heart of Enterprise', requestingInstitutionSymbol:'OCLC:AVL']);
  /*
  okapi.createRequest([
                       title:'Temeraire',
                       patronIdentifier: '905808497',
                       patronReference: 'PR',
                       patronType:'PT',
                       patronEmail:'patron@institution',
                       patronNote:'Please dont shoot the messenger',
                       pickupLocation:'A string',
                       systemInstanceIdentifier:'8a6d65a3-709c-4ade-9ffa-043fb031fedd',
                       requestingInstitutionSymbol:'ILL:KNOWINT01']);
  */

  okapi.createRequest([
                       title:'The darkening land',
                       patronIdentifier: '905808497',
                       patronReference: 'PR',
                       volume:'Please supply vol 1-2 of 23',
                       systemInstanceIdentifier:'3246e0db-6d41-442b-ae61-27f1d607a8dc',
                       requestingInstitutionSymbol:'ILL:MVS',
                       rota:[
                         [ directoryId:'ILL:TEU', rotaPosition:'0' ],
                         [ directoryId:'ILL:VLA', rotaPosition:'2' ],
                         [ directoryId:'ILL:KNOWINT', rotaPosition:'3' ]
                       ]]);

}
