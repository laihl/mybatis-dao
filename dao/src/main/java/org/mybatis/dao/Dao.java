package org.mybatis.dao;

import java.util.List;

import org.mybatis.dao.condation.Cnd;
import org.mybatis.dao.database.Table;
import org.mybatis.dao.delete.DeleteContext;
import org.mybatis.dao.delete.DeleteExcutor;
import org.mybatis.dao.delete.DynamicTableNameDeleteExcutor;
import org.mybatis.dao.exception.IdIsNullException;
import org.mybatis.dao.exception.SizeNotEqualException;
import org.mybatis.dao.insert.DynamicTableNameInsertExcutor;
import org.mybatis.dao.insert.InsertContext;
import org.mybatis.dao.insert.InsertExcutor;
import org.mybatis.dao.insert.RelationInsertExcutor;
import org.mybatis.dao.mapper.DaoMapper;
import org.mybatis.dao.selecte.DynamicTableNameSelectExcutor;
import org.mybatis.dao.selecte.FieldFilterRelationSelectExcutor;
import org.mybatis.dao.selecte.FieldFilterSelectExcutor;
import org.mybatis.dao.selecte.Page;
import org.mybatis.dao.selecte.PageSelectExcutor;
import org.mybatis.dao.selecte.RelationSelectExcutor;
import org.mybatis.dao.selecte.SelectContext;
import org.mybatis.dao.selecte.SelectExcutor;
import org.mybatis.dao.update.CondationUpdateExcutor;
import org.mybatis.dao.update.DynamicTableNameCondationUpdateExcutor;
import org.mybatis.dao.update.DynamicTableNameUpdateExcutor;
import org.mybatis.dao.update.FieldFilterUpdateExcutor;
import org.mybatis.dao.update.UpdateContext;
import org.mybatis.dao.update.UpdateExcutor;
import org.mybatis.dao.util.CollectionUtils;
import org.mybatis.dao.util.ReflectionUtils;
import org.mybatis.dao.util.Toolkit;

/**
 * @author 作者 :吴立中
 * @version 创建时间：2016年7月12日 下午4:16:39 类说明
 */
public class Dao {

	private DaoMapper daoMapper;

	private DaoConfig daoConfig;

	public Dao(DaoMapper daoMapper, DaoConfig daoConfig) {
		this.daoMapper = daoMapper;
		this.daoConfig = daoConfig;
	}

	/**
	 * 更新
	 * int
	 * @param t
	 * @return
	 */
	public <T> int update(T t) {

		Class<?> targetType = Toolkit.isCglibProxy(t) ? t.getClass().getSuperclass() : t.getClass();

		Table table = TableMap.getInstance().getTableMap(targetType);

		Object id = ReflectionUtils.getValue(t, table.getId().getField());
		if (id == null) {
			throw new IdIsNullException(targetType.getName() + " Id is null");
		}

		Condation cnd = Cnd.where(table.getId().getId(), "=", id);

		UpdateContext updateContext = new UpdateContext(t, daoConfig, daoMapper);

		updateContext.setCondation(cnd);

		UpdateExcutor updateExcutor = new UpdateExcutor();

		return updateExcutor.update(updateContext);

	}
	/**
	 * 更新动态表 对象的id必须在动态表当中，否则会更新失败
	 * int
	 * @param t
	 * @param dynamicTableName
	 * @return
	 */
	public <T> int update(T t,String dynamicTableName) {

		Class<?> targetType = Toolkit.isCglibProxy(t) ? t.getClass().getSuperclass() : t.getClass();

		Table table = TableMap.getInstance().getTableMap(targetType);

		Object id = ReflectionUtils.getValue(t, table.getId().getField());
		if (id == null) {
			throw new IdIsNullException(targetType.getName() + " Id is null");
		}

		Condation cnd = Cnd.where(table.getId().getId(), "=", id);

		UpdateContext updateContext = new UpdateContext(t, daoConfig, daoMapper);

		updateContext.setCondation(cnd);

		UpdateExcutor updateExcutor = new DynamicTableNameUpdateExcutor(dynamicTableName);

		return updateExcutor.update(updateContext);

	}
	
	public <T> int update(T t,FieldFilter filter) {

		Class<?> targetType = Toolkit.isCglibProxy(t) ? t.getClass().getSuperclass() : t.getClass();

		Table table = TableMap.getInstance().getTableMap(targetType);

		Object id = ReflectionUtils.getValue(t, table.getId().getField());
		if (id == null) {
			throw new IdIsNullException(targetType.getName() + " Id is null");
		}

		Condation cnd = Cnd.where(table.getId().getId(), "=", id);

		UpdateContext updateContext = new UpdateContext(t, daoConfig, daoMapper);

		updateContext.setCondation(cnd);

		UpdateExcutor updateExcutor = new UpdateExcutor();
		updateExcutor = new FieldFilterUpdateExcutor(filter,updateExcutor);
		return updateExcutor.update(updateContext);

	}
	
	public <T> int update(T t,String dynamicTableName,FieldFilter filter) {

		Class<?> targetType = Toolkit.isCglibProxy(t) ? t.getClass().getSuperclass() : t.getClass();

		Table table = TableMap.getInstance().getTableMap(targetType);

		Object id = ReflectionUtils.getValue(t, table.getId().getField());
		if (id == null) {
			throw new IdIsNullException(targetType.getName() + " Id is null");
		}

		Condation cnd = Cnd.where(table.getId().getId(), "=", id);

		UpdateContext updateContext = new UpdateContext(t, daoConfig, daoMapper);

		updateContext.setCondation(cnd);

		UpdateExcutor updateExcutor = new DynamicTableNameUpdateExcutor(dynamicTableName);
		updateExcutor = new FieldFilterUpdateExcutor(filter,updateExcutor);
		return updateExcutor.update(updateContext);

	}
	
	public int update(Class<?> clazz,String dynamicTableName,String[] fields,Object[] values,Condation cnd){
		if(fields == null||values == null){
			throw new NullPointerException("fields or values is null");
		}
		if(fields.length!=values.length){
			throw new SizeNotEqualException("fields length not equal values length");
		}
		UpdateContext updateContext = new UpdateContext(null, daoConfig, daoMapper);
		updateContext.setCondation(cnd);
		UpdateExcutor updateExcutor = new DynamicTableNameCondationUpdateExcutor(clazz,dynamicTableName, fields, values);
		return updateExcutor.update(updateContext);
	}
	
	public int update(Class<?> clazz,String dynamicTableName,String field,Object value,Condation cnd){
		return update(clazz,dynamicTableName,new String[]{field},new Object[]{value},cnd);
	}
	
	public int update(Class<?> clazz,String[] fields,Object[] values,Condation cnd){
		if(fields == null||values == null){
			throw new NullPointerException("fields or values is null");
		}
		if(fields.length!=values.length){
			throw new SizeNotEqualException("fields length not equal values length");
		}
		UpdateContext updateContext = new UpdateContext(null, daoConfig, daoMapper);
		updateContext.setCondation(cnd);
		UpdateExcutor updateExcutor = new CondationUpdateExcutor(clazz, fields, values);
		return updateExcutor.update(updateContext);
	}
	
	public int update(Class<?> clazz,String field,Object value,Condation cnd){
		return update(clazz,new String[]{field},new Object[]{value},cnd);
	}

	public <T> int delete(T t,String dynamicTableName) {
		Class<?> targetType = Toolkit.isCglibProxy(t) ? t.getClass().getSuperclass() : t.getClass();

		Table table = TableMap.getInstance().getTableMap(targetType);

		Object id = ReflectionUtils.getValue(t, table.getId().getField());
		if (id == null) {
			throw new IdIsNullException(targetType.getName() + " Id is null");
		}

		Condation cnd = Cnd.where(table.getId().getId(), "=", id);

		DeleteContext deleteContext = new DeleteContext(t, daoConfig, daoMapper);

		deleteContext.setCondation(cnd);

		DeleteExcutor deleteExcutor = new DynamicTableNameDeleteExcutor(dynamicTableName);

		return deleteExcutor.delete(deleteContext);
	}
	
	public <T> int delete(T t) {
		Class<?> targetType = Toolkit.isCglibProxy(t) ? t.getClass().getSuperclass() : t.getClass();

		Table table = TableMap.getInstance().getTableMap(targetType);

		Object id = ReflectionUtils.getValue(t, table.getId().getField());
		if (id == null) {
			throw new IdIsNullException(targetType.getName() + " Id is null");
		}

		Condation cnd = Cnd.where(table.getId().getId(), "=", id);

		DeleteContext deleteContext = new DeleteContext(t, daoConfig, daoMapper);

		deleteContext.setCondation(cnd);

		DeleteExcutor deleteExcutor = new DeleteExcutor();

		return deleteExcutor.delete(deleteContext);
	}

	public <T> int delete(Class<T> t, Condation cnd) {

		DeleteContext deleteContext = null;
		try {
			deleteContext = new DeleteContext(t.newInstance(), daoConfig, daoMapper);
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		deleteContext.setCondation(cnd);

		DeleteExcutor deleteExcutor = new DeleteExcutor();

		return deleteExcutor.delete(deleteContext);
	}
	
	public <T> int delete(Class<T> t,String dynamicTableName, Condation cnd) {

		DeleteContext deleteContext = null;
		try {
			deleteContext = new DeleteContext(t.newInstance(), daoConfig, daoMapper);
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		deleteContext.setCondation(cnd);

		DeleteExcutor deleteExcutor = new DynamicTableNameDeleteExcutor(dynamicTableName);

		return deleteExcutor.delete(deleteContext);
	}

	public <T> int save(T t) {

		InsertContext insertContext = new InsertContext(t, daoConfig, daoMapper);

		InsertExcutor insertExcutor = new InsertExcutor();

		insertExcutor = new RelationInsertExcutor(insertExcutor);

		return insertExcutor.insert(insertContext);
	}

	public <T> int insert(T t) {
		InsertContext insertContext = new InsertContext(t, daoConfig, daoMapper);
		InsertExcutor insert = new InsertExcutor();
		return insert.insert(insertContext);
	}
	
	public <T> int insert(T t,String dynamicTableName) {
		InsertContext insertContext = new InsertContext(t, daoConfig, daoMapper);
		InsertExcutor insert = new DynamicTableNameInsertExcutor(dynamicTableName);
		return insert.insert(insertContext);
	}

	public <T> T find(Class<T> t, long id) {
		Table table = TableMap.getInstance().getTableMap(t);
		Condation c = Cnd.where(table.getId().getId(), "=", id);
		return find(t, c);
	}

	public <T> T find(Class<T> t, Condation cnd) {
		SelectContext sc = new SelectContext(t, daoConfig, daoMapper, cnd);
		SelectExcutor select = new SelectExcutor();
		select = new RelationSelectExcutor(select);
		List<T> resultList = select.select(sc);
		return CollectionUtils.isEmpty(resultList) ? null : resultList.get(0);
	}

	public <T> T selectOne(Class<T> t, long id) {
		Table table = TableMap.getInstance().getTableMap(t);
		Condation c = Cnd.where(table.getId().getId(), "=", id);
		return selectOne(t, c);
	}

	public <T> T selectOne(Class<T> t, FieldFilter filter, long id) {
		Table table = TableMap.getInstance().getTableMap(t);
		Condation c = Cnd.where(table.getId().getId(), "=", id);
		return selectOne(t, filter, c);
	}

	public <T> T selectOne(Class<T> t, FieldFilter filter, Condation cnd) {
		SelectContext sc = new SelectContext(t, daoConfig, daoMapper, cnd);
		SelectExcutor selector = new SelectExcutor();
		selector = new FieldFilterSelectExcutor(filter,selector);
		List<T> resultList = selector.select(sc);
		return CollectionUtils.isEmpty(resultList) ? null : resultList.get(0);
	}

	public <T> T selectOne(Class<T> t, Condation cnd) {
		SelectContext sc = new SelectContext(t, daoConfig, daoMapper, cnd);
		SelectExcutor select = new SelectExcutor();
		List<T> resultList = select.select(sc);
		return CollectionUtils.isEmpty(resultList) ? null : resultList.get(0);
	}

	
	
	public <T> T selectOne(Class<T> t,String dynamicTableName, long id) {
		Table table = TableMap.getInstance().getTableMap(t);
		Condation c = Cnd.where(table.getId().getId(), "=", id);
		return selectOne(t,dynamicTableName, c);
	}

	public <T> T selectOne(Class<T> t,String dynamicTableName, FieldFilter filter, long id) {
		Table table = TableMap.getInstance().getTableMap(t);
		Condation c = Cnd.where(table.getId().getId(), "=", id);
		return selectOne(t,dynamicTableName, filter, c);
	}

	public <T> T selectOne(Class<T> t,String dynamicTableName, FieldFilter filter, Condation cnd) {
		SelectContext sc = new SelectContext(t, daoConfig, daoMapper, cnd);
		SelectExcutor selector = new DynamicTableNameSelectExcutor(dynamicTableName);
		selector = new FieldFilterSelectExcutor(filter,selector);
		List<T> resultList = selector.select(sc);
		return CollectionUtils.isEmpty(resultList) ? null : resultList.get(0);
	}

	public <T> T selectOne(Class<T> t,String dynamicTableName, Condation cnd) {
		SelectContext sc = new SelectContext(t, daoConfig, daoMapper, cnd);
		SelectExcutor selector = new DynamicTableNameSelectExcutor(dynamicTableName);
		selector = new SelectExcutor(selector);
		List<T> resultList = selector.select(sc);
		return CollectionUtils.isEmpty(resultList) ? null : resultList.get(0);
	}
	
	
	public <T> List<T> selectList(Class<T> t, Condation cnd) {
		SelectContext sc = new SelectContext(t, daoConfig, daoMapper, cnd);
		SelectExcutor select = new SelectExcutor();
		return select.select(sc);
	}

	public <T> List<T> selectList(Class<T> t, FieldFilter filter) {
		SelectContext sc = new SelectContext(t, daoConfig, daoMapper, null);
		SelectExcutor selector = new SelectExcutor();
		selector = new FieldFilterSelectExcutor(filter,selector);
		return selector.select(sc);
	}

	public <T> List<T> selectList(Class<T> t, FieldFilter filter, Condation cnd) {
		SelectContext sc = new SelectContext(t, daoConfig, daoMapper, cnd);
		SelectExcutor selector = new SelectExcutor();
		selector = new FieldFilterSelectExcutor(filter,selector);
		return selector.select(sc);
	}

	public <T> List<T> selectList(Class<T> t, FieldFilter filter, Condation cnd, Page page) {
		SelectContext sc = new SelectContext(t, daoConfig, daoMapper, cnd);
		SelectExcutor selector = new SelectExcutor();
		selector = new FieldFilterSelectExcutor(filter,selector);
		selector = new PageSelectExcutor(selector, page);
		return selector.select(sc);
	}

	public <T> List<T> selectList(Class<T> t, Condation cnd, Page page) {
		SelectContext sc = new SelectContext(t, daoConfig, daoMapper, cnd);
		SelectExcutor select = new SelectExcutor();
		select = new PageSelectExcutor(select, page);
		List<T> resultList = select.select(sc);
		return resultList;
	}
	
	public <T> List<T> selectList(Class<T> t,String dynamicTableName, Condation cnd) {
		SelectContext sc = new SelectContext(t, daoConfig, daoMapper, cnd);
		SelectExcutor selector = new DynamicTableNameSelectExcutor(dynamicTableName);
		selector = new SelectExcutor(selector);
		return selector.select(sc);
	}

	public <T> List<T> selectList(Class<T> t,String dynamicTableName, FieldFilter filter) {
		SelectContext sc = new SelectContext(t, daoConfig, daoMapper, null);
		SelectExcutor selector = new DynamicTableNameSelectExcutor(dynamicTableName);
		selector = new SelectExcutor(selector);
		selector = new FieldFilterSelectExcutor(filter,selector);
		return selector.select(sc);
	}

	public <T> List<T> selectList(Class<T> t,String dynamicTableName, FieldFilter filter, Condation cnd) {
		SelectContext sc = new SelectContext(t, daoConfig, daoMapper, cnd);
		SelectExcutor selector = new DynamicTableNameSelectExcutor(dynamicTableName);
		selector = new SelectExcutor(selector);
		selector = new FieldFilterSelectExcutor(filter,selector);
		return selector.select(sc);
	}

	public <T> List<T> selectList(Class<T> t,String dynamicTableName, FieldFilter filter, Condation cnd, Page page) {
		SelectContext sc = new SelectContext(t, daoConfig, daoMapper, cnd);
		SelectExcutor selector = new DynamicTableNameSelectExcutor(dynamicTableName);
		selector = new SelectExcutor(selector);
		selector = new FieldFilterSelectExcutor(filter,selector);
		selector = new PageSelectExcutor(selector, page);
		return selector.select(sc);
	}

	public <T> List<T> selectList(Class<T> t,String dynamicTableName, Condation cnd, Page page) {
		SelectContext sc = new SelectContext(t, daoConfig, daoMapper, cnd);
		SelectExcutor selector = new DynamicTableNameSelectExcutor(dynamicTableName);
		selector = new SelectExcutor(selector);
		selector = new PageSelectExcutor(selector, page);
		List<T> resultList = selector.select(sc);
		return resultList;
	}

	public <T> List<T> query(Class<T> t, Condation cnd, Page page) {
		SelectContext sc = new SelectContext(t, daoConfig, daoMapper, cnd);
		SelectExcutor select = new SelectExcutor();
		select = new RelationSelectExcutor(select);
		select = new PageSelectExcutor(select, page);
		return select.select(sc);
	}

	public <T> List<T> query(Class<T> t, Condation cnd) {
		SelectContext sc = new SelectContext(t, daoConfig, daoMapper, cnd);
		SelectExcutor select = new SelectExcutor();
		select = new RelationSelectExcutor(select);
		return select.select(sc);
	}

	public <T> List<T> query(Class<T> t, FieldFilter filter) {
		SelectContext sc = new SelectContext(t, daoConfig, daoMapper, null);
		SelectExcutor selector = new SelectExcutor();
		selector = new FieldFilterSelectExcutor(filter,selector);
		selector = new FieldFilterRelationSelectExcutor(filter,selector);
		return selector.select(sc);
	}
	
	public <T> List<T> query(Class<T> t, FieldFilter filter, Condation cnd) {
		SelectContext sc = new SelectContext(t, daoConfig, daoMapper, cnd);
		SelectExcutor selector = new SelectExcutor();
		selector = new FieldFilterSelectExcutor(filter,selector);
		selector = new FieldFilterRelationSelectExcutor(filter,selector);
		return selector.select(sc);
	}
	
	public <T> List<T> query(Class<T> t, FieldFilter filter, Condation cnd,Page page) {
		SelectContext sc = new SelectContext(t, daoConfig, daoMapper, cnd);
		SelectExcutor selector = new SelectExcutor();
		selector = new FieldFilterSelectExcutor(filter,selector);
		selector = new FieldFilterRelationSelectExcutor(filter,selector);
		selector = new PageSelectExcutor(selector, page);
		return selector.select(sc);
	}

}
