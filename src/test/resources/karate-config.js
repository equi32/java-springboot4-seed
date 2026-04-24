function fn() {
	var env = karate.env || 'test';
	karate.log('Karate env:', env);

	var config = {};

	// Read baseUrl from system property passed by IntegrationTestRunner
	var baseUrl = karate.properties['baseUrl'];
	if (baseUrl) {
		config.baseUrl = baseUrl;
		karate.log('Using baseUrl from system property:', baseUrl);
	} else {
		// Fallback for local development
		config.baseUrl = 'http://localhost:8080';
		karate.log('Using default baseUrl:', config.baseUrl);
	}

	// Configure timeouts
	karate.configure('connectTimeout', 10000);
	karate.configure('readTimeout', 10000);

	return config;
}
