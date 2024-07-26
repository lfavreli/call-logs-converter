import Header from "./Header";
import Footer from "./Footer";
import MainContainer from "./MainContainer";
import { Outlet } from "@tanstack/react-router"

const Page = () => {
  return (
    <>
      <Header />
      <MainContainer>
        <Outlet />
      </MainContainer>
      <Footer />
    </>
  );
}

export default Page;
