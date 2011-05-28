/*
 * Copyright (c) 2007, 2008, 2009, 2010 David Berkman
 * 
 * This file is part of the SmallMind Code Project.
 * 
 * The SmallMind Code Project is free software, you can redistribute
 * it and/or modify it under the terms of GNU Affero General Public
 * License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * The SmallMind Code Project is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the the GNU Affero General Public
 * License, along with The SmallMind Code Project. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 * Additional permission under the GNU Affero GPL version 3 section 7
 * ------------------------------------------------------------------
 * If you modify this Program, or any covered work, by linking or
 * combining it with other code, such other code is not for that reason
 * alone subject to any of the requirements of the GNU Affero GPL
 * version 3.
 */
package com.codeasylum.stress.api;

import com.codeasylum.stress.api.format.FormattingException;
import com.codeasylum.stress.api.format.HttpFormatterFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class HttpExchange implements Exchange<HttpTask> {

  private static final HttpFormatterFactory HTTP_FORMATTER_FACTORY;

  private String hostId;
  private String taskName;
  private String requestContentType;
  private String responseContentType;
  private boolean success;
  private long startMillis;
  private long stopMillis;
  private int responseCode;
  private byte[] rawRequest;
  private byte[] rawResponse;

  static {

    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("com/codeasylum/stress/api/http.xml");

    HTTP_FORMATTER_FACTORY = context.getBean("httpFormatterFactory", HttpFormatterFactory.class);
  }

  public HttpExchange (boolean success, String hostId, String taskName, long startMillis, long stopMillis, int responseCode, String requestContentType, String requestBody, String responseContentType, byte[] rawResponse) {

    this.success = success;
    this.hostId = hostId;
    this.taskName = taskName;
    this.requestContentType = requestContentType;
    this.responseContentType = responseContentType;
    this.startMillis = startMillis;
    this.stopMillis = stopMillis;
    this.responseCode = responseCode;
    this.rawResponse = rawResponse;

    rawRequest = requestBody.getBytes();
  }

  @Override
  public String getHostId () {

    return hostId;
  }

  @Override
  public Class<HttpTask> getTaskClass () {

    return HttpTask.class;
  }

  @Override
  public String getTaskName () {

    return taskName;
  }

  @Override
  public boolean isSuccess () {

    return success;
  }

  @Override
  public long getStartMillis () {

    return startMillis;
  }

  @Override
  public long getStopMillis () {

    return stopMillis;
  }

  public int getResponseCode () {

    return responseCode;
  }

  public String getRequestContentType () {

    return requestContentType;
  }

  public String getResponseContentType () {

    return responseContentType;
  }

  public byte[] getRawRequest () {

    return rawRequest;
  }

  public byte[] getRawResponse () {

    return rawResponse;
  }

  @Override
  public String getFormattedRequest ()
    throws FormattingException {

    return HTTP_FORMATTER_FACTORY.getFormatter(requestContentType).format(rawRequest);
  }

  @Override
  public String getFormattedResponse ()
    throws FormattingException {

    return HTTP_FORMATTER_FACTORY.getFormatter(responseContentType).format(rawResponse);
  }
}
