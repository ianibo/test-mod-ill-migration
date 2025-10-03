:load ./modrsCli.groovy
okapi=new OkapiClient('millersville')
rsclient = new RSClient(okapi);

okapi.addTenantSymbol('ILL:MILL');
okapi.addTenantSymbol('ILL:MVS');
