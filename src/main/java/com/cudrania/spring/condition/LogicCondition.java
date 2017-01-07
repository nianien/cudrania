package com.cudrania.spring.condition;

import org.springframework.context.annotation.Condition;

/**
 * 支持逻辑运算
 *
 * @param <Annotation> 注解类型
 * @param <Context>    用于匹配的上下文
 * @param <Value>      单项匹配类型
 * @author scorpio
 * @version 1.0.0
 */
public abstract class LogicCondition<Annotation, Context, Value> extends BaseCondition<Annotation> implements Condition {
  /**
   * 逻辑运算
   *
   * @param context
   * @param values
   * @param logic
   * @return
   */
  protected boolean matches(Context context, Value[] values, Logic logic) {
    boolean matched = logic == Logic.AND ? true : false;
    for (Value annotationAttributes : values) {
      boolean matchOne = matchOne(context, annotationAttributes);
      if (logic == Logic.AND) {
        matched &= matchOne;
        if (!matched) {
          break;
        }
      } else if (logic == Logic.OR) {
        matched |= matchOne;
        if (matched) {
          break;
        }
      } else {
        throw new IllegalArgumentException("unsupported logic:" + logic);
      }
    }
    return matched;
  }


  abstract protected boolean matchOne(Context context, Value value);


  public enum Logic {
    AND, OR
  }
}
