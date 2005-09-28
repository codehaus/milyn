<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="www.milyn.org/tld/tinak.tld" prefix="tinak" %>

<sdoc>
	<scontent id="cnn.home" src="www.cnn.com/index.html" />

	<tinak:select>
		<tinak:ua match="html">
			<tinak:select>
				<tinak:ua match="large">
					<!-- default -->
					<spage>
						<sput idref='cnn.home:*' />
					</spage>
				</tinak:ua>
				<tinak:ua match="small,medium">
					<spage title="Headline Story">
						<template:insert template='templates/html-small-medium.jsp'>
							<template:put name="body">
								<sput idref='cnn.home:html/body//div[@class="cnnMainT1Hd"]/..' />
							</template:put>
						</template:insert>
					</spage>
					<spage title="More News">
						<template:insert template='templates/html-small-medium.jsp'>
							<template:put name="body">
								<P/><STRONG>MORE NEWS</STRONG>
								<ul>
								<snav req-slinks="false">
									<snavsrc>
										<sput idref='cnn.home:html/body//div[@class="cnnMainNewT2"]/a[1]' />
									</snavsrc>
									<snavblock>
										<li><a/></li>
									</snavblock>
								</snav>	
								</ul>
							</template:put>
						</template:insert>
					</spage>
					<spage title="Business">
						<template:insert template='templates/html-small-medium.jsp'>
							<template:put name="body">
								<sput idref='cnn.home:html/body/table[4]//tr[4]/td/table[2]//table[@id="stockquotes"]/../..' />
							</template:put>
						</template:insert>
					</spage>
				</tinak:ua>
			</tinak:select>
		</tinak:ua>
		
		<tinak:ua match="wml">
			<spage>
				<template:insert template='templates/wml.jsp'>
					<template:put name="cards">
						<card>
							<sput idref='cnn.home:html/body//div[@class="cnnMainT1Hd"]' />
							<sput idref='cnn.home:html/body//div[@class="cnnMainT1"]/p[1]' />
						</card>
					</template:put>
				</template:insert>
			</spage>
		</tinak:ua>

		<tinak:ua>
			<!-- default -->
			<spage>
				Unrecognised device.
				1. Add device recognition parameters to device-ident.xml.
				2. Add device profiles to device-profiles.xml
			</spage>
		</tinak:ua>
	</tinak:select>
</sdoc>
