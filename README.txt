# README.txt
#
# Copyright (C) 2012-2021 Rafael Corchuelo.
#
# In keeping with the traditional purpose of furthering education and research, it is
# the policy of the copyright owner to permit non-commercial use and redistribution of
# this software. It has been tested carefully, but it is not guaranteed for any particular
# purposes.  The copyright owner does not offer any warranties or representations, nor do
# they accept any liabilities with respect to them.

This is the Acme Planner.

GitHub repository: https://github.com/danricost/Acme-Planner
CleverCloud credentials: gonrodter@alum.us.es

Con respecto a como cambiar el moment de los shouts para que se muestren el número de shouts que se desee (al filtrar por antigüedad de 1 mes no se muestran todos los shouts que están en el sample data), se debe de entrar en el archivo sample-data.xml con path: /Acme-Planner/src/main/webapp/WEB-INF/population/sample-data.xml. 
Una vez ahí, localizar los shouts (están al principio del archivo) y en la property con name="moment", cambiarle el value con la fecha que se desee.
