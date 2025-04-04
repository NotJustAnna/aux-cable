import {StrictMode} from 'react'
import {createRoot} from 'react-dom/client'
import './index.css'
import App from './App.tsx'
import {ApolloClient, ApolloProvider, HttpLink, InMemoryCache, split} from '@apollo/client';
import {GraphQLWsLink} from '@apollo/client/link/subscriptions';
import {createClient} from 'graphql-ws';
import {getMainDefinition} from "@apollo/client/utilities";

// const wsProto: Record<string, string> = { 'http:': 'ws:', 'https:': 'wss:' };

const client = new ApolloClient({
    link: split(
        ({query}) => {
            const definition = getMainDefinition(query);
            return (
                definition.kind === 'OperationDefinition' &&
                definition.operation === 'subscription'
            );
        },
        new GraphQLWsLink(createClient({
            // url: `${wsProto[window.location.protocol]}//${window.location.host}/graphql`,
            url: 'ws://localhost:3000/graphql',
        })),
        new HttpLink({
            // uri: `${(window.location.origin)}/graphql`
            uri: 'http://localhost:3000/graphql'
        }),
    ),
    cache: new InMemoryCache(),
});

createRoot(document.getElementById('root')!).render(
  <StrictMode>
      <ApolloProvider client={client}>
          <App />
      </ApolloProvider>
  </StrictMode>,
)
