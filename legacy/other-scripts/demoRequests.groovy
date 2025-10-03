:load ./modrsCli.groovy
okapi=new OkapiClient('reshare')
rsclient = new RSClient(okapi);
okapi.listTenantSymbols()

okapi.createRequest([
                     title:'Temeraire',
                     patronIdentifier: 'ian.ibbotson',
                     patronReference: 'IanTest04635',
                     patronSurname: 'Ibbotson',
                     patronGivenName: 'Ian',
                     pickupLocation: 'Surely this is nullable?',
                     patronType:'PT',
                     requestingInstitutionSymbol:'ILL:KNOWINT01',
                     rota:[
                         [ directoryId:'ILL:KNOWINT01', rotaPosition:'0' ]
                     ] ]);
