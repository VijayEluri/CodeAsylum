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
package com.codeasylum.stress.api;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.util.EntityUtils;
import org.smallmind.nutsnbolts.http.HttpMethod;
import org.smallmind.nutsnbolts.lang.StackTraceUtilities;
import org.smallmind.nutsnbolts.lang.UnknownSwitchCaseException;

public class HttpTask extends AbstractTask {

  private static final ConcurrentHashMap<String, Pattern> PATTERN_MAP = new ConcurrentHashMap<String, Pattern>();

  private transient HttpClient httpClient = new RedirectingHttpClient();

  private HashMap<String, String> regexpMap = new HashMap<String, String>();
  private HashMap<String, String> validationMap = new HashMap<String, String>();
  private Attribute<String> serverAttribute = new Attribute<String>(String.class);
  private Attribute<String> pathAttribute = new Attribute<String>(String.class);
  private Attribute<String> contentTypeAttribute = new Attribute<String>(String.class);
  private Attribute<String> bodyAttribute = new Attribute<String>(String.class);
  private Attribute<Integer> portAttribute = new Attribute<Integer>(Integer.class, "80");
  private HttpMethod httpMethod = HttpMethod.GET;
  private String responseKey;

  public HttpTask () {

  }

  private HttpTask (HttpTask httpTask) {

    super(httpTask);

    regexpMap = new HashMap<String, String>(httpTask.getRegexpMap());
    validationMap = new HashMap<String, String>(httpTask.getValidationMap());
    httpMethod = httpTask.getHttpMethod();
    serverAttribute = new Attribute<String>(String.class, httpTask.getServerAttribute());
    portAttribute = new Attribute<Integer>(Integer.class, httpTask.getPortAttribute());
    pathAttribute = new Attribute<String>(String.class, httpTask.getPathAttribute());
    contentTypeAttribute = new Attribute<String>(String.class, httpTask.getContentTypeAttribute());
    bodyAttribute = new Attribute<String>(String.class, httpTask.getBodyAttribute());
    responseKey = httpTask.getResponseKey();
  }

  public HashMap<String, String> getRegexpMap () {

    return regexpMap;
  }

  public void setRegexpMap (HashMap<String, String> regexpMap) {

    this.regexpMap = regexpMap;
  }

  public HashMap<String, String> getValidationMap () {

    return validationMap;
  }

  public void setValidationMap (HashMap<String, String> validationMap) {

    this.validationMap = validationMap;
  }

  public Attribute<String> getServerAttribute () {

    return serverAttribute;
  }

  public void setServerAttribute (Attribute<String> serverAttribute) {

    this.serverAttribute = serverAttribute;
  }

  public Attribute<String> getPathAttribute () {

    return pathAttribute;
  }

  public void setPathAttribute (Attribute<String> pathAttribute) {

    this.pathAttribute = pathAttribute;
  }

  public Attribute<String> getContentTypeAttribute () {

    return contentTypeAttribute;
  }

  public void setContentTypeAttribute (Attribute<String> contentTypeAttribute) {

    this.contentTypeAttribute = contentTypeAttribute;
  }

  public Attribute<String> getBodyAttribute () {

    return bodyAttribute;
  }

  public void setBodyAttribute (Attribute<String> bodyAttribute) {

    this.bodyAttribute = bodyAttribute;
  }

  public Attribute<Integer> getPortAttribute () {

    return portAttribute;
  }

  public void setPortAttribute (Attribute<Integer> portAttribute) {

    this.portAttribute = portAttribute;
  }

  public HttpMethod getHttpMethod () {

    return httpMethod;
  }

  public void setHttpMethod (HttpMethod httpMethod) {

    this.httpMethod = httpMethod;
  }

  public String getResponseKey () {

    return responseKey;
  }

  public void setResponseKey (String responseKey) {

    this.responseKey = responseKey;
  }

  @Override
  public void execute (String hostId, Ouroboros ouroboros, ExchangeTransport exchangeTransport)
    throws IOException, ScriptInterpolationException {

    if (isEnabled() && ouroboros.isEnabled()) {

      HttpUriRequest httpRequest;
      ResponseCarrier responseCarrier;
      URI requestURI;
      StringBuilder uriBuilder;
      String requestMimeType;
      String requestCharSet;
      String requestBody;
      boolean validated = true;
      long startTime;

      if ((portAttribute.getScript() == null) || (portAttribute.getScript().length() == 0)) {
        throw new TaskExecutionException("The %s(%s) has no http port value configured", HttpTask.class.getSimpleName(), getName());
      }

      uriBuilder = new StringBuilder("http://").append(serverAttribute.get(this)).append(':').append(portAttribute.get(this)).append('/').append(pathAttribute.get(this));
      try {
        requestURI = new URI(uriBuilder.toString());
      }
      catch (URISyntaxException uriSyntaxException) {
        throw new TaskExecutionException(uriSyntaxException, "The %s(%s) has been configured with a malformed URI(%s)", HttpTask.class.getSimpleName(), getName(), uriBuilder.toString());
      }

      if ((requestMimeType = contentTypeAttribute.get(this)) != null) {

        int semiColonPos;

        if ((semiColonPos = requestMimeType.indexOf(';')) < 0) {
          requestCharSet = "utf-8";
        }
        else {

          int equalsPos;

          if ((equalsPos = requestMimeType.indexOf('=', semiColonPos + 1)) <= 0) {
            throw new TaskExecutionException("The %s(%s) contains an improperly formatted content type(%s)", HttpTask.class.getSimpleName(), getName(), requestMimeType);
          }

          requestCharSet = requestMimeType.substring(equalsPos + 1);
          requestMimeType = requestMimeType.substring(0, semiColonPos);
        }
      }
      else {
        requestMimeType = "text/plain";
        requestCharSet = "utf-8";
      }

      switch (httpMethod) {
        case GET:
          if (((requestBody = bodyAttribute.get(this)) != null) && (requestBody.length() > 0)) {
            throw new TaskExecutionException("The %s(%s) uses the 'GET' method, but has been configured with body content", HttpTask.class.getSimpleName(), getName());
          }

          httpRequest = new HttpGet(requestURI);
          break;
        case PUT:
          if (((requestBody = bodyAttribute.get(this)) == null) || (requestBody.length() == 0)) {
            throw new TaskExecutionException("The %s(%s) uses the 'PUT' method, but has not been configured with any body content", HttpTask.class.getSimpleName(), getName());
          }

          httpRequest = new HttpPut(requestURI);
          ((HttpPut)httpRequest).setEntity(new StringEntity(requestBody, requestCharSet));
          break;
        case POST:
          if (((requestBody = bodyAttribute.get(this)) == null) || (requestBody.length() == 0)) {
            throw new TaskExecutionException("The %s(%s) uses the 'POST' method, but has not been configured with any body content", HttpTask.class.getSimpleName(), getName());
          }

          httpRequest = new HttpPost(requestURI);
          ((HttpPost)httpRequest).setEntity(new StringEntity(requestBody, requestCharSet));
          break;
        case DELETE:
          if (((requestBody = bodyAttribute.get(this)) != null) && (requestBody.length() > 0)) {
            throw new TaskExecutionException("The %s(%s) uses the 'DELETE' method, but has been configured with body content", HttpTask.class.getSimpleName(), getName());
          }

          httpRequest = new HttpDelete(requestURI);
          break;
        default:
          throw new UnknownSwitchCaseException(httpMethod.name());
      }

      httpRequest.setHeader("Content-Type", requestMimeType + ";charset=" + requestCharSet);

      startTime = System.currentTimeMillis();
      try {
        responseCarrier = httpClient.execute(httpRequest, new ResponseHandler<ResponseCarrier>() {

          @Override
          public ResponseCarrier handleResponse (HttpResponse response)
            throws IOException {

            HttpEntity entity;
            Header contentTypeHeader = response.getFirstHeader("Content-Type");
            String responseMimeType;
            String responseCharSet;

            if ((contentTypeHeader != null) && ((responseMimeType = contentTypeHeader.getValue()) != null)) {

              int semiColonPos;

              if ((semiColonPos = responseMimeType.indexOf(';')) < 0) {
                responseCharSet = "utf-8";
              }
              else {

                int equalsPos;

                if ((equalsPos = responseMimeType.indexOf('=', semiColonPos + 1)) <= 0) {
                  throw new TaskExecutionException("Improperly formatted content type(%s) in response", responseMimeType);
                }

                responseCharSet = responseMimeType.substring(equalsPos + 1);
                responseMimeType = responseMimeType.substring(0, semiColonPos);
              }
            }
            else {
              responseMimeType = "text/plain";
              responseCharSet = "utf-8";

            }

            return new ResponseCarrier(System.currentTimeMillis(), response.getStatusLine().getStatusCode(), responseMimeType, responseCharSet, ((entity = response.getEntity()) == null) ? null : EntityUtils.toByteArray(entity));
          }
        });

        if (!regexpMap.isEmpty()) {

          Matcher regexpMatcher;
          String responseBody = (responseCarrier.getRawResponse() == null) ? null : new String(responseCarrier.getRawResponse(), responseCarrier.getResponseCharSet());

          for (Map.Entry<String, String> regexpEntry : regexpMap.entrySet()) {
            PropertyContext.removeKeysStartingWith(regexpEntry.getKey());

            if (responseBody != null) {
              if ((regexpMatcher = getPattern(regexpEntry.getValue()).matcher(responseBody)).find()) {
                PropertyContext.put(regexpEntry.getKey(), "true");
                for (int groupIndex = 0; groupIndex <= regexpMatcher.groupCount(); groupIndex++) {
                  PropertyContext.put(regexpEntry.getKey() + '.' + groupIndex, regexpMatcher.group(groupIndex));
                }
              }
            }
          }
        }

        if (!validationMap.isEmpty()) {
          for (Map.Entry<String, String> validationEntry : validationMap.entrySet()) {
            if (!PropertyContext.valueEquals(validationEntry.getKey(), validationEntry.getValue())) {
              validated = false;
              break;
            }
          }
        }

        if ((responseKey != null) && (responseKey.length() > 0)) {
          PropertyContext.put(responseKey, new String(responseCarrier.getRawResponse()));
        }

        exchangeTransport.send(new HttpExchange(validated && (responseCarrier.getResponseCode() == 200), hostId, getName(), startTime, responseCarrier.getResponseTimestamp(), responseCarrier.getResponseCode(), requestMimeType, requestCharSet, requestBody, responseCarrier.getResponseMimeType(), responseCarrier.getResponseCharSet(), responseCarrier.getRawResponse()));
      }
      catch (Exception exception) {
        exchangeTransport.send(new HttpExchange(false, hostId, getName(), startTime, System.currentTimeMillis(), 503, requestMimeType, requestCharSet, requestBody, "text/plain", "utf-8", StackTraceUtilities.obtainStackTraceAsString(exception).getBytes()));

        if (!regexpMap.isEmpty()) {
          for (Map.Entry<String, String> regexpEntry : regexpMap.entrySet()) {
            PropertyContext.removeKeysStartingWith(regexpEntry.getKey());
          }
        }

        if ((responseKey != null) && (responseKey.length() > 0)) {
          PropertyContext.remove(responseKey);
        }
      }
    }
  }

  private Pattern getPattern (String primalPattern) {

    Pattern pattern;

    if ((pattern = PATTERN_MAP.get(primalPattern)) == null) {
      PATTERN_MAP.put(primalPattern, pattern = Pattern.compile(primalPattern));
    }

    return pattern;
  }

  @Override
  public Task deepCopy () {

    return new HttpTask(this);
  }

  private Object readResolve () {

    httpClient = new DefaultHttpClient();

    return this;
  }

  private class RedirectingHttpClient extends DefaultHttpClient {

    public RedirectingHttpClient () {

      super(new SingleClientConnManager());

      setRedirectStrategy(new DefaultRedirectStrategy());
    }
  }

  private class ResponseCarrier {

    private String responseMimeType;
    private String responseCharSet;
    private byte[] rawResponse;
    private long responseTimestamp;
    private int responseCode;

    private ResponseCarrier (long responseTimestamp, int responseCode, String responseMimeType, String responseCharSet, byte[] rawResponse) {

      this.responseTimestamp = responseTimestamp;
      this.responseCode = responseCode;
      this.responseMimeType = responseMimeType;
      this.responseCharSet = responseCharSet;
      this.rawResponse = rawResponse;
    }

    public long getResponseTimestamp () {

      return responseTimestamp;
    }

    public int getResponseCode () {

      return responseCode;
    }

    public String getResponseMimeType () {

      return responseMimeType;
    }

    public void setResponseMimeType (String responseMimeType) {

      this.responseMimeType = responseMimeType;
    }

    public String getResponseCharSet () {

      return responseCharSet;
    }

    public void setResponseCharSet (String responseCharSet) {

      this.responseCharSet = responseCharSet;
    }

    public byte[] getRawResponse () {

      return rawResponse;
    }
  }
}
