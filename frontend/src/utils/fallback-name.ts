export const fallbackName = (name: string) => {
    return name.split(" ", 3).map(word => word.charAt(0)).join("");
}