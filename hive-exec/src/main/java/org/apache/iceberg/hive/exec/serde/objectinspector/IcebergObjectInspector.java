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

import java.util.List;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.typeinfo.PrimitiveTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;
import org.apache.iceberg.Schema;
import org.apache.iceberg.types.Type;
import org.apache.iceberg.types.TypeUtil;
import org.apache.iceberg.types.Types;

public final class IcebergObjectInspector extends TypeUtil.SchemaVisitor<ObjectInspector> {

  public static ObjectInspector create(Schema schema) {
    return TypeUtil.visit(schema, new IcebergObjectInspector());
  }

  @Override
  public ObjectInspector field(Types.NestedField field, ObjectInspector fieldObjectInspector) {
    return fieldObjectInspector;
  }

  @Override
  public ObjectInspector list(Types.ListType listTypeInfo, ObjectInspector listObjectInspector) {
    return ObjectInspectorFactory.getStandardListObjectInspector(listObjectInspector);
  }

  @Override
  public ObjectInspector map(Types.MapType mapType,
                             ObjectInspector keyObjectInspector, ObjectInspector valueObjectInspector) {
    return ObjectInspectorFactory.getStandardMapObjectInspector(keyObjectInspector, valueObjectInspector);
  }

  @Override
  public ObjectInspector primitive(Type.PrimitiveType primitiveType) {
    final PrimitiveTypeInfo primitiveTypeInfo;

    switch (primitiveType.typeId()) {
      case BINARY:
        return IcebergBinaryObjectInspector.get();
      case BOOLEAN:
        primitiveTypeInfo = TypeInfoFactory.booleanTypeInfo;
        break;
      case DATE:
        return IcebergDateObjectInspector.get();
      case DECIMAL:
        Types.DecimalType type = (Types.DecimalType) primitiveType;
        return IcebergDecimalObjectInspector.get(type.precision(), type.scale());
      case DOUBLE:
        primitiveTypeInfo = TypeInfoFactory.doubleTypeInfo;
        break;
      case FLOAT:
        primitiveTypeInfo = TypeInfoFactory.floatTypeInfo;
        break;
      case INTEGER:
        primitiveTypeInfo = TypeInfoFactory.intTypeInfo;
        break;
      case LONG:
        primitiveTypeInfo = TypeInfoFactory.longTypeInfo;
        break;
      case STRING:
        primitiveTypeInfo = TypeInfoFactory.stringTypeInfo;
        break;
      case TIMESTAMP:
        boolean adjustToUTC = ((Types.TimestampType) primitiveType).shouldAdjustToUTC();
        return IcebergTimestampObjectInspector.get(adjustToUTC);

      case FIXED:
      case TIME:
      case UUID:
      default:
        throw new IllegalArgumentException(primitiveType.typeId() + " type is not supported");
    }

    return PrimitiveObjectInspectorFactory.getPrimitiveJavaObjectInspector(primitiveTypeInfo);
  }

  @Override
  public ObjectInspector schema(Schema schema, ObjectInspector structObjectInspector) {
    return structObjectInspector;
  }

  @Override
  public ObjectInspector struct(Types.StructType structType, List<ObjectInspector> fieldObjectInspectors) {
    return new IcebergRecordObjectInspector(structType, fieldObjectInspectors);
  }

}
