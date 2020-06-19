/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.iceberg.hive.exec.serde.objectinspector;

import java.sql.Date;
import java.time.LocalDate;
import org.apache.hadoop.hive.serde2.io.DateWritable;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.DateObjectInspector;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;

public final class IcebergDateObjectInspector extends IcebergPrimitiveObjectInspector implements DateObjectInspector {

  private static final IcebergDateObjectInspector INSTANCE = new IcebergDateObjectInspector();

  public static IcebergDateObjectInspector get() {
    return INSTANCE;
  }

  private IcebergDateObjectInspector() {
    super(TypeInfoFactory.dateTypeInfo);
  }

  @Override
  public Date getPrimitiveJavaObject(Object o) {
    return o == null ? null : Date.valueOf((LocalDate) o);
  }

  @Override
  public DateWritable getPrimitiveWritableObject(Object o) {
    Date date = getPrimitiveJavaObject(o);
    return date == null ? null : new DateWritable(date);
  }

  @Override
  public Object copyObject(Object o) {
    return o == null ? null : new Date(((Date) o).getTime());
  }

}
