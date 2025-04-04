import {Card, Centered, LinkButton, MonospacedInput, Separator, SuccessButton, Title} from "./common.tsx";
import {useRef} from "react";
import {gql, useMutation} from "@apollo/client";

const LOGIN = gql`
    mutation Login($token: String!) {
        login(token: $token, remember: false) {
            type
        }
    }
`;

export function IdlePage() {
    const [loginFunction, { loading, error }] = useMutation(LOGIN);
    const botTokenRef = useRef<HTMLInputElement>(null);
    const login = () => {
        console.log("Logging in...");
        const token = botTokenRef.current!!.value;
        loginFunction({variables: {token}});
    };
    return <Card>
        <Title>Aux Cable</Title>
        <Separator/>
        <div className="my-4 flex flex-column gap-4">
            <div className="py-2">
                <label htmlFor="idle:botToken" className="font-semibold">Bot Token</label>
            </div>
            <div className="">
                <MonospacedInput ref={botTokenRef} id="idle:botToken" placeholder="Bot Token..." />
                <LinkButton>I don't have a bot token...</LinkButton>
            </div>
        </div>
        <Centered><SuccessButton onClick={login}>Login</SuccessButton></Centered>
    </Card>
}