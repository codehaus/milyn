/**
 *
 */
package org.milyn.persistence;



/**
 * This is my experimental class, which contains some of my experiments
 *
 * @author maurice
 *
 */

public class ExperimentalTest {

//	private final static Log log = LogFactory.getLog(ExperimentalTest.class);
//
//	EntityManager em;
//
//	Query query;
//
//	@SuppressWarnings("unchecked")
//	DAORegistery registery;
//
//	AnnotatedDAO annotatedDAO;
//
//	Writer reportWriter;
//
//
//    @SuppressWarnings("unchecked")
//	final IAnswer returnFirstArgumentAnswer = new IAnswer() {
//
//		public Object answer() throws Throwable {
//			return EasyMock.getCurrentArguments()[0];
//		}
//
//	};
//
//	@BeforeTest
//	public void before() {
//
//		log.debug("before");
//
//		em = createMock(EntityManager.class);
//		query = createMock(Query.class);
//		registery = createMock(DAORegistery.class);
//		annotatedDAO = createMock(AnnotatedDAO.class);
//	}
//
//	@Test(enabled=true)
//    public void testAnnotatedDAO() {
//
//		new EasyMockTemplate(registery, annotatedDAO) {
//
//			@SuppressWarnings("unchecked")
//			@Override
//			protected void expectations() {
//
//				final City city = new City();
//				city.name = "Test";
//
//				final Collection<City> qAssocciateResult = new ArrayList<City>(1);
//				qAssocciateResult.add(city);
//
//				expect(registery.getDAO("Person")).andReturn(annotatedDAO);
//
//				expect(annotatedDAO.findByQuery(isA(String.class), isA(Object[].class))).andReturn((Collection) qAssocciateResult);
//
//				registery.returnDAO(annotatedDAO);
//
//				expect(registery.getDAO("Person")).andReturn(annotatedDAO);
//
//				expect(annotatedDAO.mergeIt(isA(Person.class))).andAnswer(returnFirstArgumentAnswer);
//
//				annotatedDAO.flushIt();
//
//				registery.returnDAO(annotatedDAO);
//			}
//
//			@Override
//			protected void codeToTest() {
//
//				try {
//
//					createWriter(new File("target/test/report/report.html"));
//
//					final byte[] messageIn = readInputMessage("test.xml");
//
//					// Instantiate Smooks with the config...
//					final Smooks smooks = new Smooks("dao-smooks-config.xml");
//
//					 // Create an exec context - no profiles....
//					final ExecutionContext executionContext = smooks.createExecutionContext();
//
//					PersistenceUtil.setDAORegistery(executionContext, registery);
//
//					final ExecutionEventListener eventListener = new HtmlReportGenerator(reportWriter);
//
//					executionContext.setEventListener(eventListener);
//
//					// The result of this transform is a set of Java objects...
//					final JavaResult result = new JavaResult();
//
//					// Filter the input message to extract, using the execution context...
//					smooks.filter(new StreamSource(new ByteArrayInputStream(messageIn)), result, executionContext);
//
//					final Object personObj = result.getBean("person");
//
//					assertThat(personObj)
//						.isNotNull()
//						.isInstanceOf(Person.class);
//
//					final Person person = (Person) personObj;
//
//					assertThat(person.getSurname()).isEqualTo("Test");
//
//				} catch (final SmooksException e) {
//					throw new TestExceptionWrapper(e);
//				} catch (final IOException e) {
//					throw new TestExceptionWrapper(e);
//				} catch (final SAXException e) {
//					throw new TestExceptionWrapper(e);
//				} finally {
//					closeWriter();
//				}
//
//
//			}
//
//
//		}.run();
//
//	}
//
//
//	@Test(enabled=false)
//    public void testInterfaceDAO() {
//
//		new EasyMockTemplate(em, query) {
//
//
//			@Override
//			protected void expectations()  {
//
//				final City city = new City();
//				city.name = "Test";
//
//				final List<City> qAssocciateResult = new ArrayList<City>(1);
//				qAssocciateResult.add(city);
//
//				expect(em.createQuery(isA(String.class))).andReturn(query);
//				expect(query.setParameter(1, city.name)).andReturn(query);
//				expect(query.getResultList()).andReturn(qAssocciateResult);
//
//				expect(em.merge(isA(Person.class))).andAnswer(returnFirstArgumentAnswer);
//
//				em.flush();
//
//			}
//
//			@Override
//			protected void codeToTest() {
//
//
//					try {
//
//						final byte[] messageIn = readInputMessage("test.xml");
//
//						// Instantiate Smooks with the config...
//						final Smooks smooks = new Smooks("dao-smooks-config.xml");
//
//						 // Create an exec context - no profiles....
//						final ExecutionContext executionContext = smooks.createExecutionContext();
//
//						PersistenceUtil.setDAORegistery(executionContext, new EntityManagerRegistery(em));
//
//						// The result of this transform is a set of Java objects...
//						final JavaResult result = new JavaResult();
//
//						// Filter the input message to extract, using the execution context...
//						smooks.filter(new StreamSource(new ByteArrayInputStream(messageIn)), result, executionContext);
//
//						final Object personObj = result.getBean("person");
//
//						assertThat(personObj)
//							.isNotNull()
//							.isInstanceOf(Person.class);
//
//						final Person person = (Person) personObj;
//
//						assertThat(person.getSurname()).isEqualTo("Test");
//
//					} catch (final SmooksException e) {
//						throw new TestExceptionWrapper(e);
//					} catch (final IOException e) {
//						throw new TestExceptionWrapper(e);
//					} catch (final SAXException e) {
//						throw new TestExceptionWrapper(e);
//					}
//
//
//			}
//		}.run();
//
//    }
//
//	private void createWriter(final File file) {
//		try {
//			log.info(file.getAbsolutePath());
//			file.mkdirs();
//			if(file.exists()) {
//				file.delete();
//			}
//			file.createNewFile();
//
//			reportWriter = new BufferedWriter(new FileWriter(file));
//		} catch (final IOException e) {
//			throw new TestExceptionWrapper(e);
//		}
//	}
//
//	private void closeWriter() {
//		try {
//			reportWriter.close();
//		} catch (final IOException e) {
//			throw new TestExceptionWrapper(e);
//		}
//	}
//
//
//    private static byte[] readInputMessage(final String file) {
//        try {
//
//            return StreamUtils.readStream(ClassLoader.getSystemResourceAsStream(file));
//        } catch (final IOException e) {
//            e.printStackTrace();
//            return "<no-message/>".getBytes();
//        }
//    }


}
