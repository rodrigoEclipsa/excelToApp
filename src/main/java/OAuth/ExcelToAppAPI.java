package OAuth;

import java.util.Map;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.extractors.TokenExtractor;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthConfig;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;


public class ExcelToAppAPI extends DefaultApi20
{

	@Override
	public String getAccessTokenEndpoint()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getAuthorizationBaseUrl()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OAuth20Service createService(OAuthConfig config)
	{
		// TODO Auto-generated method stub
		return super.createService(config);
	}

	@Override
	public TokenExtractor<OAuth2AccessToken> getAccessTokenExtractor()
	{
		// TODO Auto-generated method stub
		return super.getAccessTokenExtractor();
	}

	@Override
	public Verb getAccessTokenVerb()
	{
		// TODO Auto-generated method stub
		return super.getAccessTokenVerb();
	}

	@Override
	public String getAuthorizationUrl(OAuthConfig config,
			Map<String, String> additionalParams)
	{
		// TODO Auto-generated method stub
		return super.getAuthorizationUrl(config, additionalParams);
	}

	@Override
	public String getRefreshTokenEndpoint()
	{
		// TODO Auto-generated method stub
		return super.getRefreshTokenEndpoint();
	}

	
	
	
}


