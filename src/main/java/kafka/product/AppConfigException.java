// ----------------------------------------------------------------------------
// Copyright 2018, BIGDATA
// All rights reserved
// ----------------------------------------------------------------------------
// Change History:
//  2018.01.01  datnh
//     - Initial release
// ----------------------------------------------------------------------------
package kafka.product;

/**
 * <p>
 * Title: NEIF SERVICE
 * </p>
 * <p>
 * Copyright: Copyright (c) by BIGDATA 2018
 * </p>
 * 
 * @author bigdata
 * @version 0.1
 */
public class AppConfigException extends Exception {
	private static final long serialVersionUID = -6111091783023868367L;

	public AppConfigException() {
		super();
	}

	public AppConfigException(String message) {
		super(message);
	}

	public AppConfigException(Throwable throwable) {
		super(throwable);
	}

	public AppConfigException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
