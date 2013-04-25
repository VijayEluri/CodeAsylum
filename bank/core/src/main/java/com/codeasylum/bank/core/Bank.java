/*
 * Copyright (c) 2007, 2008, 2009, 2010, 2011 David Berkman
 * 
 * This file is part of the CodeAsylum Code Project.
 * 
 * The CodeAsylum Code Project is free software, you can redistribute
 * it and/or modify it under the terms of GNU Affero General Public
 * License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * The CodeAsylum Code Project is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the the GNU Affero General Public
 * License, along with The CodeAsylum Code Project. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 * Additional permission under the GNU Affero GPL version 3 section 7
 * ------------------------------------------------------------------
 * If you modify this Program, or any covered work, by linking or
 * combining it with other code, such other code is not for that reason
 * alone subject to any of the requirements of the GNU Affero GPL
 * version 3.
 */
package com.codeasylum.bank.core;

import java.io.IOException;
import java.nio.file.FileSystems;
import com.codeasylum.bank.core.topology.Topology;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Bank {

  public Bank (String configDir)
    throws IOException {

    Topology topology;

    if ((topology = Topology.read(FileSystems.getDefault().getPath(configDir))) == null) {
      System.out.println("Go get topology");
    }

    System.out.println(topology);
  }

  public static void main (String... args) {

    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("com/codeasylum/bank/core/bank.xml", "com/codeasylum/bank/core/logging.xml", "com/codeasylum/bank/core/paxos.xml");
  }
}
