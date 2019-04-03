package com.cudrania.hibernate;

import com.cudrania.hibernate.transformer.BeanResultTransFormer;
import com.cudrania.hibernate.transformer.MapResultTransFormer;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;

/**
 * 基于Hibernate 5.x的数据库访问实现<br/>
 *
 * @author skyfalling
 * @see #setParameters(Query, Object...)
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
    public <T> List<T> getAll(Class<T> entityClass) {
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
    public <T, K extends Serializable> List<T> getByIds(Class<T> entityClass, List<K> ids) {
        if (CollectionUtils.isEmpty(ids))
            return Collections.EMPTY_LIST;
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
    public <T> List<T> getByProperties(Class<T> entityClass, Map<String, Object> properties) {
        if (properties == null || properties.isEmpty())
            return Collections.EMPTY_LIST;
        entityClass = getEntityClass(entityClass);
        return createCriteria(entityClass).add(Restrictions.allEq(properties)).list();
    }


    /**
     * 根据ID删除实体对象<br/>
     * 这里要求entity对象的ID属性不能为空
     *
     * @param entity
     */
    @Transactional
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
    @Transactional
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
    @Transactional
    public <T> T save(T entity) {
        return (T) getSession().merge(entity);
    }

    /**
     * 保存实体对象列表
     *
     * @param entities
     * @param <T>
     */
    @Transactional
    public <T> void save(List<T> entities) {
        for (T entity : entities) {
            save(entity);
        }
    }

    /**
     * SQL查询,返回Map对象列表,其中key为字段别名,value为字段值<br/>
     *
     * @param sql
     * @param parameters
     * @return
     * @see #setParameters(Query, Object...)
     */
    public List<Map<String, ?>> sqlQuery(String sql, Object... parameters) {
        return createSQLQuery(sql, parameters).setResultTransformer(new MapResultTransFormer()).list();
    }


    /**
     * SQL查询,返回指定类型对象列表<br/>
     *
     * @param beanClass
     * @param sql
     * @param parameters
     * @param <T>
     * @return
     * @see #setParameters(Query, Object...)
     */
    public <T> List<T> sqlQuery(Class<T> beanClass, String sql, Object... parameters) {
        return setQueryType(createSQLQuery(sql, parameters), beanClass).list();
    }


    /**
     * 执行HQL查询<br/>
     *
     * @param hql
     * @param parameters
     * @return
     * @see #setParameters(Query, Object...)
     */
    public List hqlQuery(String hql, Object... parameters) {
        return createQuery(hql, parameters).list();
    }

    /**
     * 分页SQL查询,返回Map对象列表,其中key为字段别名,value为字段值<br/>
     * 如果page对象满足
     * <blockquote><code>page.isAutoCount() && page.getTotalCount() == 0</code></blockquote>
     * 则进行自动计算总页数
     *
     * @param page
     * @param sql
     * @param parameters
     * @return
     * @see #setParameters(Query, Object...)
     */
    public Page<Map<String, ?>> sqlPageQuery(Page page, String sql, Object... parameters) {
        Query query = createSQLQuery(sql, parameters).setResultTransformer(new MapResultTransFormer());
        pageQuery(query, page);
        if (page.isAutoCount() && page.getTotalCount() == 0) {
            page.setTotalCount(countSql(sql, parameters));
        }
        return page;
    }


    /**
     * 分页SQL查询,返回指定类型对象列表<br/>
     * 如果page对象满足
     * <blockquote><code>page.isAutoCount() && page.getTotalCount() == 0</code></blockquote>
     * 则进行自动计算总页数
     *
     * @param page
     * @param beanClass
     * @param sql
     * @param parameters
     * @param <T>
     * @return
     * @see #setParameters(Query, Object...)
     */
    public <T> Page<T> sqlPageQuery(Page<T> page, Class<T> beanClass, String sql, Object... parameters) {
        NativeQuery query = setQueryType(createSQLQuery(sql, parameters), beanClass);
        pageQuery(query, page);
        if (page.isAutoCount() && page.getTotalCount() == 0) {
            page.setTotalCount(countSql(sql, parameters));
        }
        return page;
    }

    /**
     * 执行HQL分页查询<br/>
     * 如果page对象满足
     * <blockquote><code>page.isAutoCount() && page.getTotalCount() == 0</code></blockquote>
     * 则进行自动计算总页数
     *
     * @param page
     * @param hql
     * @param parameters
     * @return
     * @see #setParameters(Query, Object...)
     */
    public Page hqlPageQuery(Page page, String hql, Object... parameters) {
        Query query = createQuery(hql, parameters);
        pageQuery(query, page);
        if (page.isAutoCount() && page.getTotalCount() == 0) {
            page.setTotalCount(countHql(hql, parameters));
        }
        return page;
    }


    /**
     * 分页查询
     *
     * @param query
     * @param page  分页对象
     * @return
     */
    public static <T> Page<T> pageQuery(Query query, Page<T> page) {
        query.setFirstResult(
                (page.getPageNo() - 1) * page.getPageSize()
        ).setMaxResults(page.getPageSize());
        page.setResult(query.list());
        return page;
    }


    /**
     * SQL查询,返回单个Map对象,其中key为字段别名,value为字段值.如果查询结果不存在,则返回null;如果查询结果存在多条,则抛出异常.<br/>
     *
     * @param sql
     * @param parameters
     * @return
     * @see #setParameters(Query, Object...)
     */
    public Map<String, ?> sqlUniqueQuery(String sql, Object... parameters) {
        List<Map<String, ?>> list = sqlQuery(sql, parameters);
        if (list.size() > 1)
            throw new RuntimeException("more than one results occurs!");
        return list.isEmpty() ? null : list.get(0);
    }


    /**
     * SQL查询,返回单个对象.如果查询结果不存在,则返回null;如果查询结果存在多条,则抛出异常.<br/>
     *
     * @param sql
     * @param parameters
     * @return
     * @see #setParameters(Query, Object...)
     */
    public <T> T sqlUniqueQuery(Class<T> beanClass, String sql, Object... parameters) {
        List<T> list = sqlQuery(beanClass, sql, parameters);
        if (list.size() > 1)
            throw new RuntimeException("more than one results occurs!");
        return list.isEmpty() ? null : list.get(0);
    }


    /**
     * 执行HQL查询,返回单个结果.如果查询结果不存在,则返回null;如果查询结果存在多条,则抛出异常.<br/>
     *
     * @param hql
     * @param parameters
     * @return
     * @see #setParameters(Query, Object...)
     */
    public Object hqlUniqueQuery(String hql, Object... parameters) {
        List list = hqlQuery(hql, parameters);
        if (list.size() > 1)
            throw new RuntimeException("more than one results occurs!");
        return list.isEmpty() ? null : list.get(0);
    }


    /**
     * 统计SQL查询总数<br/>
     * 这里的SQL不含limit关键字
     *
     * @param sql
     * @param parameters
     * @return
     */
    public long countSql(String sql, Object... parameters) {
        Number number = (Number) createSQLQuery("SELECT COUNT(1) count FROM (" + sql + ") tmp__", parameters).addScalar("count", StandardBasicTypes.LONG).uniqueResult();
        return number.longValue();
    }

    /**
     * 统计HQL总数<br/>
     * 这里的HQL不含limit关键字
     */
    public long countHql(String hql, Object... parameters) {
        String countHql = "SELECT COUNT(*) " + hql;
        Number number = (Number) hqlUniqueQuery(countHql, parameters);
        return number.longValue();
    }


    /**
     * 创建{@link SQLQuery}对象
     *
     * @param sql
     * @param parameters
     * @return
     * @see #setParameters(Query, Object...)
     */
    public NativeQuery createSQLQuery(String sql, Object... parameters) {
        return setParameters(getSession().createSQLQuery(sql), parameters);
    }

    /**
     * 创建{@link Query}对象<br/>
     *
     * @param hql
     * @param parameters
     * @return
     * @see #setParameters(Query, Object...)
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
     * 执行SQL语句
     *
     * @param sql
     * @param parameters
     * @return the number of entities updated or deleted.
     * @see #setParameters(Query, Object...)
     */
    @Transactional
    public int execute(String sql, Object... parameters) {
        return createSQLQuery(sql, parameters).executeUpdate();
    }

    /**
     * 批量执行多条SQL语句
     *
     * @param sqlList
     */
    @Transactional
    public void executeBatch(final String... sqlList) {
        getSession().doWork(connection -> {
            Statement stmt = connection.createStatement();
            for (String sql : sqlList) {
                stmt.addBatch(sql);
            }
            stmt.executeBatch();
        });
    }


    /**
     * 批量执行SQL语句<br/>
     * 同一SQL模板,不同参数组
     *
     * @param sql
     * @param parameters
     */
    @Transactional
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
     * 根据SQL(HQL)语句和参数类列表创建SQLQuery对象<br/>
     * 参数可以是任意类型,针对不同类型参数,处理逻辑不同
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
     * 注意
     * <ol>
     * <li>
     * 如果SQL(HQL)语句中只包含JDBC占位符?,则要求占位符与参数个数一致,并按索引位置依次赋值.此时不支持in操作
     * </li>
     * <li>
     * 如果存在同名JPA参数,则占位符优先级高于Map和POJO对象
     * </li>
     * <li>
     * 对于Map和POJO对象,如果存在相同命名参数,则按照参数位置顺序,后面的参数会覆盖前面的参数
     * </li>
     * </ol>
     * <p/>
     * 示例:
     * <pre>
     * <code>Map<String, Object> map = new HashMap<String, Object>();
     * map.put("idList", new Long[]{1L, 2L, 3L});
     * map.put("title", "title1%");
     * EventAlias event = new EventAlias();
     * event.setTitle("title2%");
     * String sql = "select e1.event_id ID,e1.event_date Date,e1.title,e1.type from event e1 where e1.title like :title or e1.title like ?2 and e1.event_id in :idList";
     * List&lt;EventAlias> list = hibernateDao.sqlQuery(EventAlias.class, sql,map,event,"title3%");
     * //select e1.event_id ID,e1.event_date Date,e1.title,e1.type from event e1 where e1.title like 'title2%' or e1.title like 'title3%' and e1.event_id in (1,2,3)</code>
     * </pre>
     *
     * @param query
     * @param parameters
     * @param <T>
     * @return
     * @see #isSimple(java.lang.Object)
     * @see #simpleTypes
     */
    public <T extends Query> T setParameters(T query, Object... parameters) {
        if (query.getNamedParameters().length == 0) {
            for (int i = 0; i < parameters.length; i++) {
                query.setParameter(i, parameters[i]);
            }
            return query;
        }
        Map<String, Object> jpaMap = new HashMap<String, Object>();
        for (int i = 0; i < parameters.length; i++) {
            Object param = parameters[i];
            if (param instanceof Collection || isSimple(param) || param.getClass().isArray()) {
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
     * 判断参数是否为简单类型
     *
     * @param value
     * @return
     */
    protected boolean isSimple(Object value) {
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
     * 设置Query对象类型
     *
     * @param query
     * @param beanClass
     * @return
     */
    private NativeQuery setQueryType(NativeQuery query, Class beanClass) {
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

    /**
     * 转换primitive数组
     *
     * @param value
     * @return
     */
    private static Object handle(Object value) {
        if (value.getClass().isArray()) {
            return ObjectUtils.toObjectArray(value);
        }
        return value;
    }


    /**
     * 简单类型列表
     */
    protected static final Set<Class> simpleTypes = new HashSet<Class>(Arrays.asList(
            //primitive和包装类
            boolean.class,
            byte.class,
            double.class,
            float.class,
            int.class,
            long.class,
            short.class,
            Boolean.class,
            Byte.class,
            Double.class,
            Float.class,
            Integer.class,
            Long.class,
            Short.class,

            //常用简单类型
            String.class,
            BigDecimal.class,
            BigInteger.class,
            Number.class,
            Date.class,
            Time.class,
            Timestamp.class,


            //数据对象类型
            Blob.class,
            Clob.class,
            InputStream.class,
            Reader.class,
            Ref.class,
            SQLXML.class,
            URL.class,

            //class类型
            Class.class
    ));

}
