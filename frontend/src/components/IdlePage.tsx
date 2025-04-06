import {
    ButtonGroup,
    Card,
    Centered,
    GroupButton,
    LinkButton,
    MonospacedInput,
    Separator,
    SuccessButton,
    Title
} from "./common.tsx";
import {useRef, useState} from "react";
import {AccountModel} from "../model.ts";
import {useHttp} from "../contexts/HttpContext.ts";
import {useMountEffect} from "../useMountEffect.ts";
import {errorStrings} from "../errorStrings.ts";
import {usePostMessage} from "../contexts/MessageContext.tsx";
import {faRightToBracket} from "@fortawesome/free-solid-svg-icons/faRightToBracket";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";

export function IdlePage() {
    const postMessage = usePostMessage();
    const http = useHttp();
    const [accounts, setAccounts] = useState([] as AccountModel[]);
    const [showAccounts, setShowAccounts] = useState(false);

    useMountEffect(() => {
        http.get("/accounts").then(it => {
            const wasEmpty = accounts.length === 0;
            setAccounts(it.data);
            if (it.data.length > 0 && wasEmpty) {
                setShowAccounts(true);
            }
        });
    });

    const botTokenRef = useRef<HTMLInputElement>(null);
    const rememberMeRef = useRef<HTMLInputElement>(null);

    const loginWithToken = () => {
        const token = botTokenRef.current!!.value;
        const remember = rememberMeRef.current!!.checked;
        http.post('/actions/login', { token, remember }).then(res => {
            if (res.status >= 400 && res.status <= 500) {
                postMessage({ type: 'ERROR', content: errorStrings[res.data.errorType] ?? res.data.errorType });
            }
        })
    };

    const loginWithId = (id: string) => {
        http.post('/actions/login', { token: `RememberMe.id=${id}`, remember: true }).then(res => {
            if (res.status >= 400 && res.status <= 500) {
                postMessage({ type: 'ERROR', content: errorStrings[res.data.errorType] ?? res.data.errorType });
            }
        })
    }

    return <Card>
        <Title>Aux Cable</Title>
        <Separator/>
        {
            accounts.length > 0 && <ButtonGroup>
                <GroupButton side="left" active={showAccounts} onClick={() => setShowAccounts(true)}>Accounts</GroupButton>
                <GroupButton side="right" active={!showAccounts} onClick={() => setShowAccounts(false)}>Token</GroupButton>
            </ButtonGroup>
        }
        {
            showAccounts && accounts.map((account, i) =>
                <div className="bg-slate-700 text-white border border-slate-600 rounded-md py-2 px-3 flex gap-3 justify-items-center" key={i}>
                    <img src={account.imageUrl} alt="" className="rounded-full max-w-12 max-h-12 aspect-square"/>
                    <div className="flex flex-col justify-center flex-1">
                        <h2 className="mb-0.5 text-lg font-semibold">{account.name}</h2>
                    </div>
                    <SuccessButton className="my-0.75 rounded-lg" onClick={() => loginWithId(account.id)}><FontAwesomeIcon icon={faRightToBracket} /></SuccessButton>
                </div>)
        }
        {
            !showAccounts && <>
                <div className="my-4 flex flex-column gap-4">
                    <label htmlFor="idle:botToken" className="block py-2 font-semibold">Bot Token</label>
                    <div className="">
                        <MonospacedInput ref={botTokenRef} id="idle:botToken" placeholder="Bot Token..." />
                        <LinkButton>I don't have a bot token...</LinkButton>
                        <div className="pt-2 flex flex-row gap-2">
                            <input ref={rememberMeRef} type="checkbox" className="w-4"/>
                            <span>Remember Account</span>
                        </div>
                    </div>
                </div>
                <Centered><SuccessButton onClick={loginWithToken}>Login</SuccessButton></Centered>
            </>
        }
    </Card>
}