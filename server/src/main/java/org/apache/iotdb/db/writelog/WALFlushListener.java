/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.iotdb.db.writelog;

import java.io.IOException;
import org.apache.iotdb.db.conf.IoTDBDescriptor;
import org.apache.iotdb.db.engine.flush.FlushListener;
import org.apache.iotdb.db.engine.memtable.IMemTable;
import org.apache.iotdb.db.engine.storagegroup.TsFileProcessor;

public class WALFlushListener implements FlushListener {

  private TsFileProcessor processor;

  public WALFlushListener(TsFileProcessor processor) {
    this.processor = processor;
  }

  @Override
  public void onFlushStart(IMemTable memTable) throws IOException {
    if (IoTDBDescriptor.getInstance().getConfig().isEnableWal()) {
      processor.getLogNode().notifyStartFlush();
    }
  }

  @Override
  public void onFlushEnd(IMemTable memTable) {
    if (!memTable.isSignalMemTable() && IoTDBDescriptor.getInstance().getConfig().isEnableWal()) {
      processor.getLogNode().notifyEndFlush();
    }
  }
}
