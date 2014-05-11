package com.kavas.misc.hibernate;

import com.kavas.misc.hibernate.transformer.BeanResultTransFormer;
import com.kavas.misc.hibernate.transformer.MapResultTransFormer;
import com.kavas.misc.utils.Page;
import org.hibernate.*;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;
import org.hibernate.metadata.ClassMetadata;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.Map.Entry;

/**
 * 基于Hibernate的数据库访问实现<br/>
 * 在进行SQL(HQL)查询时,针对不同类型参数对象,处理逻辑如下:
 * <ul>
 * <li>简单类型<br/>
 * 参数是否为简单类型由{@link #isSimple(Object)}方法判定,默认简单类型列表由{@link #simpleTypes}字段表示, 针对简单类型的参数,根据索引位置将其赋值给形如"?n"的JPA参数</li>
 * <li>Map对象<br/>
 * 根据键值key值将其对应的value值赋值给形如":key"同名命名参数</li>
 * <li>POJO对象<br/>
 * 类型Map对象,根据属性名称将属性值赋值给同名命名参数</li>
 * <li>数组或集合<br/>
 * 对于简单类型参数数组或集合,支持in操作<br/>
 * 对于Map对象的value值或者POJO对象的属性值,如果为数组或集合类型,同样支持in操作</li>
 * </ul>
 * 此外,如果SQL(HQL)语句中只包含JDBC占位符?,则要求占位符与参数个数一致,然后依次赋值,需要注意的是,此时不支持in操作
 * <p/>
 * 示例:
 * <pre>
 * <code>Map<String, Object> map = new HashMap<String, Object>();
 * map.put("idList", new Long[]{1L, 2L, 3L});
 * map.put("title", "%测试2%");
 * EventAlias event = new EventAlias();
 * event.setTitle("%测试%");
 * sql = "select e1.event_id ID,e1.event_date Date,e1.title,e1.type from event e1 where e1.title like :title or e1.title like ?1 and e1.event_id in :idList";
 * list = hibernateDao.sqlQuery(EventAlias.class, sql,event, "%中国%", map);</code>
 * </pre>
 *
 * @author skyfalling
 */
@Repository
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class HibernateDao {

    @Resource
    protected SessionFactory sessionFactory;

    /**
     * 获取SessionFactory
     *
     * @return
     */
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * 设置SessionFactory
     *
     * @param sessionFactory
     */
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * 获取当前session
     *
     * @return
     */
    public Session getSession() {
        return sessionFactory.getCurrentSession();
    }


    /**
     * 根据ID查询实体对象<br/>
     * 这里要求entity对象的ID属性不能为空
     *
     * @param entity
     * @param <T>
     * @return
     */
    public <T> T get(T entity) {
        Class entityClass = getEntityClass(entity);
        Serializable id = getClassMetadata(entityClass).getIdentifier(entity, null);
        return (T) get(entityClass, id);
    }


    /**
     * 根据ID查询实体对象
     *
     * @param entityClass
     * @param id
     * @param <T>
     * @param <K>
     * @return
     */
    public <T, K extends Serializable> T get(Class<T> entityClass, K id) {
        entityClass = getEntityClass(entityClass);
        return (T) getSession().get(entityClass, id);
    }


    /**
     * 获取指定类型的全部实体对象
     *
     * @param entityClass
     * @param <T>
     * @return
     */
    public <T> List<T> findAll(Class<T> entityClass) {
        entityClass = getEntityClass(entityClass);
        return createCriteria(entityClass).list();
    }


    /**
     * 根据ID列表查询实体对象
     *
     * @param entityClass
     * @param ids
     * @param <T>
     * @param <K>
     * @return
     */
    public <T, K extends Serializable> List<T> findByIds(Class<T> entityClass, List<K> ids) {
        entityClass = getEntityClass(entityClass);
        String idName = getClassMetadata(entityClass).getIdentifierPropertyName();
        return createCriteria(entityClass).add(Restrictions.in(idName, ids)).list();
    }


    /**
     * 根据属性值查询实体对象
     *
     * @param entityClass
     * @param properties
     * @param <T>
     * @return
     */
    public <T> List<T> findByProperties(Class<T> entityClass, Map<String, Object> properties) {
        entityClass = getEntityClass(entityClass);
        return createCriteria(entityClass).add(Restrictions.allEq(properties)).list();
    }


    /**
     * 根据ID删除实体对象<br/>
     * 这里要求entity对象的ID属性不能为空
     *
     * @param entity
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void delete(Object entity) {
        getSession().delete(entity);
    }


    /**
     * 根据ID删除实体对象<br/>
     *
     * @param entityClass
     * @param id
     * @param <K>
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public <K extends Serializable> void delete(Class entityClass, K id) {
        delete(getClassMetadata(entityClass).instantiate(id, null));
    }


    /**
     * 保存实体对象
     *
     * @param entity
     * @param <T>
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public <T> T save(T entity) {
        return (T) getSession().merge(entity);
    }

    /**
     * 保存实体对象列表
     *
     * @param entities
     * @param <T>
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public <T> void save(List<T> entities) {
        for (T entity : entities) {
            save(entity);
        }
    }

    /**
     * SQL查询,返回Map对象列表,其中key为字段别名,value为字段值<br/>
     * 参数赋值参考{@link #setParameters(org.hibernate.Query, Object...)}方法
     *
     * @param sql
     * @param parameters
     * @return
     * @see #setParameters(org.hibernate.Query, Object...)
     */
    public List<Map<String, ?>> sqlQuery(String sql, Object... parameters) {
        return createSQLQuery(sql, parameters).setResultTransformer(new MapResultTransFormer()).list();
    }


    /**
     * SQL查询,返回指定类型对象列表<br/>
     * 参数赋值参考{@link #setParameters(org.hibernate.Query, Object...)}方法
     *
     * @param beanClass
     * @param sql
     * @param parameters
     * @param <T>
     * @return
     * @see #setParameters(org.hibernate.Query, Object...)
     */
    public <T> List<T> sqlQuery(Class<T> beanClass, String sql, Object... parameters) {
        return setQueryType(createSQLQuery(sql, parameters), beanClass).list();
    }


    /**
     * 执行HQL查询<br/>
     * 参数赋值参考{@link #setParameters(org.hibernate.Query, Object...)}方法
     *
     * @param hql
     * @param parameters
     * @return
     * @see #setParameters(org.hibernate.Query, Object...)
     */
    public List hqlQuery(String hql, Object... parameters) {
        return createQuery(hql, parameters).list();
    }

    /**
     * 分页SQL查询,返回Map对象列表,其中key为字段别名,value为字段值<br/>
     * 参数赋值参考{@link #setParameters(org.hibernate.Query, Object...)}方法
     *
     * @param page
     * @param sql
     * @param parameters
     * @return
     */
    public Page<Map<String, ?>> sqlPageQuery(Page page, String sql, Object... parameters) {
        Query query = createSQLQuery(sql, parameters).setResultTransformer(new MapResultTransFormer());
        page.setResult(
                pageQuery(query, page.getPageNo(), page.getPageSize())
        );
        return page;
    }


    /**
     * 分页SQL查询,返回指定类型对象列表<br/>
     * 参数赋值参考{@link #setParameters(org.hibernate.Query, Object...)}方法
     *
     * @param page
     * @param beanClass
     * @param sql
     * @param parameters
     * @param <T>
     * @return
     */
    public <T> Page<T> sqlPageQuery(Page<T> page, Class<T> beanClass, String sql, Object... parameters) {
        SQLQuery query = setQueryType(createSQLQuery(sql, parameters), beanClass);
        page.setResult(pageQuery(query, page.getPageNo(), page.getPageSize()));
        return page;
    }

    /**
     * 执行HQL分页查询
     *
     * @param page
     * @param hql
     * @param parameters
     * @return
     */
    public Page hqlPageQuery(Page page, String hql, Object... parameters) {
        Query query = createQuery(hql, parameters);
        page.setResult(pageQuery(query, page.getPageNo(), page.getPageSize()));
        return page;
    }


    /**
     * 分页查询
     *
     * @param query
     * @param pageNo   页码
     * @param pageSize 每页数据大小
     * @return
     */
    public static List pageQuery(Query query, int pageNo, int pageSize) {
        return query.setFirstResult((pageNo - 1) * pageSize).setMaxResults(pageSize).list();
    }

    /**
     * 创建{@link SQLQuery}对象
     *
     * @param sql
     * @param parameters
     * @return
     * @see #setParameters(org.hibernate.Query, Object...)
     */
    public SQLQuery createSQLQuery(String sql, Object... parameters) {
        return setParameters(getSession().createSQLQuery(sql), parameters);
    }

    /**
     * 创建{@link Query}对象<br/>
     *
     * @param hql
     * @param parameters
     * @return
     * @see #setParameters(org.hibernate.Query, Object...)
     */
    public Query createQuery(String hql, Object... parameters) {
        return setParameters(getSession().createQuery(hql), parameters);
    }


    /**
     * 根据实体类型创建{@link Criteria}对象
     *
     * @param entityClass
     * @param <T>
     * @return
     */
    public <T> Criteria createCriteria(Class<T> entityClass) {
        return getSession().createCriteria(getEntityClass(entityClass));
    }


    /**
     * 批量执行多条SQL语句
     *
     * @param sqlList
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void executeBatch(final String... sqlList) {
        getSession().doWork(new Work() {

            public void execute(Connection connection) throws SQLException {
                Statement stmt = connection.createStatement();
                for (String sql : sqlList) {
                    stmt.addBatch(sql);
                }
                stmt.executeBatch();
            }
        });
    }


    /**
     * 批量执行SQL语句<br/>
     * 同一SQL模板,不同参数组
     *
     * @param sql
     * @param parameters
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void executeBatch(final String sql, final Object[][] parameters) {
        getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
                PreparedStatement stmt = connection.prepareStatement(sql);
                for (Object[] arr : parameters) {
                    int i = 1;
                    for (Object p : arr) {
                        stmt.setObject(i++, p);
                    }
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
        });
    }

    /**
     * 根据SQL(HQL)语句和参数类列表创建SQLQuery对象,这里SQL(HQL)参数可以是任意类型<br/>
     * 针对不同类型参数对象,处理逻辑如下:
     * <ul>
     * <li>简单类型<br/>
     * 参数是否为简单类型由{@link #isSimple(Object)}方法判定,默认简单类型列表由{@link #simpleTypes}字段表示, 针对简单类型的参数,根据索引位置将其赋值给形如"?n"的JPA参数</li>
     * <li>Map对象<br/>
     * 根据键值key值将其对应的value值赋值给形如":key"同名命名参数</li>
     * <li>POJO对象<br/>
     * 类型Map对象,根据属性名称将属性值赋值给同名命名参数</li>
     * <li>数组或集合<br/>
     * 对于简单类型参数数组或集合,支持in操作<br/>
     * 对于Map对象的value值或者POJO对象的属性值,如果为数组或集合类型,同样支持in操作</li>
     * </ul>
     * 此外,如果SQL(HQL)语句中只包含JDBC占位符?,则要求占位符与参数个数一致,然后依次赋值,需要注意的是,此时不支持in操作
     * <p/>
     * 示例:
     * <pre>
     * <code>Map<String, Object> map = new HashMap<String, Object>();
     * map.put("idList", new Long[]{1L, 2L, 3L});
     * map.put("title", "%测试2%");
     * EventAlias event = new EventAlias();
     * event.setTitle("%测试%");
     * sql = "select e1.event_id ID,e1.event_date Date,e1.title,e1.type from event e1 where e1.title like :title or e1.title like ?1 and e1.event_id in :idList";
     * list = hibernateDao.sqlQuery(EventAlias.class, sql,event, "%中国%", map);</code>
     * </pre>
     *
     * @param query
     * @param parameters
     * @param <T>
     * @return
     */
    public static <T extends Query> T setParameters(T query, Object... parameters) {
        if (query.getNamedParameters().length == 0) {
            for (int i = 0; i < parameters.length; i++) {
                query.setParameter(i, parameters[i]);
            }
            return query;
        }
        Map<String, Object> jpaMap = new HashMap<String, Object>();
        for (int i = 0; i < parameters.length; i++) {
            Object param = parameters[i];
            if (param == null || param instanceof Collection || isSimple(param) || param.getClass().isArray()) {
                jpaMap.put(String.valueOf(i), handle(param));
            } else if (param instanceof Map) {
                query.setProperties(namedParameters((Map) param));
            } else {
                query.setProperties(param);
            }
        }
        query.setProperties(jpaMap);
        return query;
    }


    /**
     * 设置Query对象类型
     *
     * @param query
     * @param beanClass
     * @return
     */
    private SQLQuery setQueryType(SQLQuery query, Class beanClass) {
        if (getClassMetadata(beanClass) != null) {
            query.addEntity(beanClass);
        } else {
            query.setResultTransformer(BeanResultTransFormer.get(beanClass));
        }
        return query;
    }


    /**
     * 将Map中的primitive类型数组转换为对象数组,以支持In操作
     *
     * @param map
     * @return
     */
    private static Map namedParameters(Map map) {
        for (Object o : map.entrySet()) {
            Entry en = (Entry) o;
            en.setValue(handle(en.getValue()));
        }
        return map;
    }

    /**
     * 获取实体对象的真实类型,避免Spring代理对象
     *
     * @param entity
     * @return
     */
    private static Class getEntityClass(Object entity) {
        if (entity instanceof Class) {
            return ClassUtils.getUserClass((Class) entity);
        }
        return ClassUtils.getUserClass(entity);
    }


    /**
     * @param entityClass 实体类型
     * @return
     */
    private ClassMetadata getClassMetadata(Class entityClass) {
        return getSessionFactory().getClassMetadata(entityClass);
    }

    private static Object handle(Object value) {
        if (value != null && value.getClass().isArray()) {
            return ObjectUtils.toObjectArray(value);
        }
        return value;
    }

    /**
     * 判断参数是否为简单类型
     *
     * @param value
     * @return
     */
    protected static boolean isSimple(Object value) {
        Class type = value.getClass();
        if (type.isArray() || simpleTypes.contains(type))
            return true;
        for (Class clazz : simpleTypes) {
            if (clazz.isInstance(value))
                return true;
        }
        return false;
    }

    /**
     * 简单类型列表
     */
    protected static final Set<Class> simpleTypes = new HashSet<Class>();

    {
        //primitive和包装类
        simpleTypes.add(boolean.class);
        simpleTypes.add(byte.class);
        simpleTypes.add(byte[].class);
        simpleTypes.add(double.class);
        simpleTypes.add(float.class);
        simpleTypes.add(int.class);
        simpleTypes.add(long.class);
        simpleTypes.add(short.class);
        simpleTypes.add(Boolean.class);
        simpleTypes.add(Byte.class);
        simpleTypes.add(Double.class);
        simpleTypes.add(Float.class);
        simpleTypes.add(Integer.class);
        simpleTypes.add(Long.class);
        simpleTypes.add(Short.class);

        //常用简单类型
        simpleTypes.add(String.class);
        simpleTypes.add(BigDecimal.class);
        simpleTypes.add(BigInteger.class);
        simpleTypes.add(Number.class);
        simpleTypes.add(Date.class);
        simpleTypes.add(Time.class);
        simpleTypes.add(Timestamp.class);


        //数据对象类型
        simpleTypes.add(Blob.class);
        simpleTypes.add(Clob.class);
        simpleTypes.add(InputStream.class);
        simpleTypes.add(Reader.class);
        simpleTypes.add(Ref.class);
        simpleTypes.add(SQLXML.class);
        simpleTypes.add(URL.class);

        //class类型
        simpleTypes.add(Class.class);
    }
}
